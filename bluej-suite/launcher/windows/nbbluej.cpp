/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <io.h>
#include <fcntl.h>
#include <process.h>
#include <commdlg.h>

static char* getUserHomeFromRegistry(char* userhome);
static char* GetStringValue(HKEY key, const char *name);
static DWORD GetDWordValue(HKEY key, const char *name);
static void parseConfigFile(const char* path);
static void parseArgs(int argc, char *argv[]);
static int readClusterFile(const char* path);
static int dirExists(const char* path);

static char userdir[MAX_PATH] = "c:\\nbuser";
static char options[4098] = "";
static char dirs[4098] = "", extradirs[4098];
static char jdkswitch[MAX_PATH] = "";
static char platformDir[MAX_PATH] = "";
static char* defaultDirs[512];
static char msg[MAX_PATH + 128];

#ifdef WINMAIN

static void parseCommandLine(char *argstr);

int WINAPI
    WinMain (HINSTANCE /* hSelf */, HINSTANCE /* hPrev */, LPSTR cmdline, int /* nShow */) {
#else
    int main(int argc, char* argv[]) {
        char cmdline[10240] = "";
        
        for (int i = 1; i < argc; i++) {
            char buf[10240];
            sprintf(buf, "\"%s\" ", argv[i]);
            strcat(cmdline, buf);
        }
#endif    

    char topdir[MAX_PATH];
    char buf[MAX_PATH * 10], *pc;
  
    GetModuleFileName(0, buf, sizeof buf);

    pc = strrchr(buf, '\\');
    if (pc != NULL) {             // always holds
        strlwr(pc + 1);
        *pc = '\0';	// remove .exe filename
    }

    pc = strrchr(buf, '\\');
    if (pc != NULL && 0 == stricmp("\\bin", pc))
        *pc = '\0';
    strcpy(topdir, buf);

    char clusterFileName[MAX_PATH];
    sprintf(clusterFileName, "%s\\etc\\nbbluej.clusters", topdir);

    if (!readClusterFile(clusterFileName)) {
        sprintf(msg, "Cannot read cluster file: %s", clusterFileName);
        MessageBox(NULL, msg, "Error", MB_ICONSTOP | MB_OK);
	exit(1);
    }

    sprintf(buf, "%s\\etc\\nbbluej.conf", topdir);
    parseConfigFile(buf);

#ifdef WINMAIN
    parseCommandLine(cmdline);
#else
    parseArgs(argc - 1, argv + 1); // skip progname
#endif    

    char olduserdir[MAX_PATH];
    strcpy(olduserdir, userdir);
    sprintf(buf, "%s\\etc\\nbbluej.conf", userdir);
    parseConfigFile(buf);
    strcpy(userdir, olduserdir);

    char nbexec[MAX_PATH];
    char cmdline2[10240];

    dirs[0] = '\0';
    for (char **pdir = defaultDirs; *pdir != NULL; pdir++) {
        sprintf(buf, "%s\\%s", topdir, *pdir);
        if (dirExists(buf)) {
            if (dirs[0] != '\0') {
                sprintf(buf, "%s;%s\\%s", dirs, topdir, *pdir);
            }
            strcpy(dirs, buf);
        }
    }
    
    if (extradirs[0] != '\0') {
        strcat(strcat(dirs, ";"), extradirs);
    }

    if (*platformDir == '\0') {
        MessageBox(NULL, "Undefined platform cluster!", "Error", MB_ICONSTOP | MB_OK);
        exit(1);
    }

    sprintf(nbexec, "%s\\%s", topdir, platformDir);

    if (!dirExists(nbexec)) {
        sprintf(msg, "Could not find platform cluster:\n\n%s", nbexec);
        MessageBox(NULL, msg, "Error", MB_ICONSTOP | MB_OK);
        exit(1);
    }        

    strcat(nbexec, "\\lib\\nbexec.exe");

    sprintf(cmdline2, "\"%s\" %s -J-Dnetbeans.importclass=org.netbeans.bluej.upgrade.AutoUpgrade -J-Dnetbeans.accept_license_class=org.netbeans.bluej.license.AcceptLicense --branding nbbluej --clusters \"%s\" --userdir \"%s\" %s %s",
            nbexec,
            jdkswitch,
            dirs,
            userdir,
            options,
            cmdline);

    STARTUPINFO start;
    PROCESS_INFORMATION pi;

    memset (&start, 0, sizeof (start));
    start.cb = sizeof (start);

#ifdef WINMAIN
    start.dwFlags = STARTF_USESHOWWINDOW;
    start.wShowWindow = SW_HIDE;
#endif
    
    if (!CreateProcess (NULL, cmdline2,
                        NULL, NULL, TRUE, NORMAL_PRIORITY_CLASS,
                        NULL, NULL,
                        &start,
                        &pi)) {
        MessageBox(NULL, "Cannot start the IDE", "Error", MB_ICONSTOP | MB_OK);
        exit(1);
    } else {
        // Wait until child process exits.
        WaitForSingleObject( pi.hProcess, INFINITE );

        // Close process and thread handles. 
        CloseHandle( pi.hProcess );
        CloseHandle( pi.hThread );        
        exit(0);
    }
    return 0;
}
    
char* getUserHomeFromRegistry(char* userhome)
{
    HKEY key;

    if (RegOpenKeyEx(
            HKEY_CURRENT_USER,
            "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders",
            0,
            KEY_READ,
            &key) != 0)
        return NULL;

    char *path = GetStringValue(key, "Desktop");
    RegCloseKey(key);
    
    char *pc = strrchr(path, '\\');
    if (pc == NULL) {
	return NULL;
    }
    *pc = '\0';
    strcpy(userhome, path);
    return userhome;
}


char * GetStringValue(HKEY key, const char *name)
{
    DWORD type, size;
    char *value = 0;

    if (RegQueryValueEx(key, name, 0, &type, 0, &size) == 0 && type == REG_SZ) {
        value = (char*) malloc(size);
        if (RegQueryValueEx(key, name, 0, 0, (unsigned char*)value, &size) != 0) {
            free(value);
            value = 0;
        }
    }
    return value;
}


DWORD GetDWordValue(HKEY key, const char *name)
{
    DWORD type, size;
    DWORD value = 0;
    
    if (RegQueryValueEx(key, name, 0, &type, 0, &size) == 0 && type == REG_DWORD) {
        if (RegQueryValueEx(key, name, 0, 0, (LPBYTE)&value, &size) != 0) {
            return 0;
        }
    }
    return value;
}


void parseConfigFile(const char* path) {
    FILE* fin = fopen(path, "r");
    if (fin == NULL)
        return;
    
    char line[2048], *pc;
    
    while (NULL != fgets(line, sizeof line, fin)) {
        for (pc = line; *pc != '\0' && (*pc == ' ' || *pc == '\t' || *pc == '\n' || *pc == '\r'); pc++)
            ;
        if (*pc == '#')
            continue;
        if (strstr(pc, "netbeans_default_userdir=") == pc) {
            char *q = strstr(pc, "=") + 1;
            pc = line + strlen(line) - 1;
            while (*pc == '\n' || *pc == '\r' || *pc == '\t' || *pc == ' ')
                pc--;

            if (*q == '"' && *pc == '"') {
                q++;
                pc--;
            }
                
            *(pc+1) = '\0';
            if (strstr(q, "${HOME}") == q) {
                char userhome[MAX_PATH];
                char *userprofile = getenv("USERPROFILE");

                if (userprofile == NULL) {
		    userprofile = getUserHomeFromRegistry(userhome);
                }
                if (userprofile != NULL) {
		    strcpy(userdir, userprofile);
                }
                // else {
                //   ... keep the default userdir value 'c:\nbuser'
                // }
                strcat(userdir, q + strlen("${HOME}"));
            } else {
                strcpy(userdir, q);
            }
        } else if (strstr(pc, "netbeans_default_options=") == pc) {
            char *q = strstr(pc, "=") + 1;
            pc = line + strlen(line) - 1;
            while (*pc == '\n' || *pc == '\r' || *pc == '\t' || *pc == ' ')
                pc--;
            
            if (*q == '"' && *pc == '"') {
                q++;
                pc--;
            }
            
            *(pc+1) = '\0';
            strcpy(options, q);
        } else if (strstr(pc, "netbeans_extraclusters=") == pc) {
            char *q = strstr(pc, "=") + 1;
            pc = line + strlen(line) - 1;
            while (*pc == '\n' || *pc == '\r' || *pc == '\t' || *pc == ' ')
                pc--;
            
            if (*q == '"' && *pc == '"') {
                q++;
                pc--;
            }
            
            *(pc+1) = '\0';
            strcpy(extradirs, q);
        } else if (strstr(pc, "netbeans_jdkhome=") == pc) {
            char *q = strstr(pc, "=") + 1;
            pc = line + strlen(line) - 1;
            while (*pc == '\n' || *pc == '\r' || *pc == '\t' || *pc == ' ')
                pc--;
            
            if (*q == '"' && *pc == '"') {
                q++;
                pc--;
            }
            
            *(pc+1) = '\0';
            sprintf(jdkswitch, "--jdkhome \"%s\"", q);
        }
    }
    fclose(fin);
}

#ifdef WINMAIN

void parseCommandLine(char *argstr) {
    char **argv = (char**) malloc(2048 * sizeof (char*));
    int argc = 0;

#define START 0
#define NORMAL 1
#define IN_QUOTES 2
#define IN_APOS 3

    char *p, *q;
    int state = NORMAL;
    char token[1024 * 64];
    int eof;
  
    q = argstr;
    p = token;
    state = START;
    eof = 0;
  
    while (!eof) {
        if (*q == '\0')
            eof = 1;
    
        switch (state) {
            case START:
                if (*q == ' ' || *q == '\r' || *q == '\n' || *q == '\t') {
                    q++;
                    continue;
                }

                p = token;
                *p = '\0';
      
                if (*q == '"') {
                    state = IN_QUOTES;
                    q++;
                    continue;
                }
                if (*q == '\'') {
                    state = IN_APOS;
                    q++;
                    continue;
                }
      
                state = NORMAL;
                continue;

            case NORMAL:
                if (*q == ' ' || *q == '\r' || *q == '\n' || *q == '\t' || *q == '\0') {
                    *p = '\0';
                    argv[argc] = strdup(token);
                    argc++;
                    state = START;
                } else {
                    *p++ = *q++;
                }
                break;

            case IN_QUOTES:
                if (*q == '"') {
                    if (*(q+1) == '"') {
                        *p++ = '"';
                        q += 2;
                    } else {
                        *p = '\0';
          
                        argv[argc] = strdup(token);
                        argc++;

                        q++;
                        state = START;
                    }
                } else if (*q == '\0') {
                    *p = '\0';
                    argv[argc] = strdup(token);
                    argc++;
                    state = START;
                } else {
                    *p++ = *q++;
                }
                break;
      
            case IN_APOS:
                if (*q == '\'') {
                    if (*(q+1) == '\'') {
                        *p++ = '\'';
                        q += 2;
                    } else {
                        *p = '\0';
          
                        argv[argc] = strdup(token);
                        argc++;

                        q++;
                        state = START;
                    }
                } else if (*q == '\0') {
                    *p = '\0';
                    argv[argc] = strdup(token);
                    argc++;
                    state = START;
                } else {
                    *p++ = *q++;
                }
                break;
        }
    }
  
    parseArgs(argc, argv);

}
#endif // WINMAIN

void parseArgs(int argc, char *argv[]) {
    char *arg;

    while (argc > 0 && (arg = *argv) != 0) {
        argv++;
        argc--;

        if (0 == strcmp("-userdir", arg) || 0 == strcmp("--userdir", arg)) {
            if (argc > 0) {
                arg = *argv;
                argv++;
                argc--;
                if (arg != 0) {
                    strcpy(userdir, arg);
                }
            }
        }
    }
}


int readClusterFile(const char* path) {

    char **dirs = defaultDirs;

    FILE* fin = fopen(path, "r");
    if (fin == NULL)
        return 0;
    
    char line[2048], *pc;
    
    while (NULL != fgets(line, sizeof line, fin)) {
        for (pc = line; *pc != '\0' && (*pc == ' ' || *pc == '\t' || *pc == '\n' || *pc == '\r'); pc++)
            ;
        if (*pc == '#')
            continue;

        char *s = pc;

	while (*pc != '\0' && *pc != '\t' && *pc != '\n' && *pc != '\r')
	    pc++;

	*pc = '\0';

	if (!strncmp("platform", s, 8) && *platformDir == '\0') {
	    strcpy(platformDir, s);
        }
        else {
	    *dirs = strdup(s);
	    dirs++;
        }
    }
    *dirs = NULL;
    fclose(fin);

    return 1;
}


int dirExists(const char* path) {
    WIN32_FIND_DATA ffd;
    HANDLE ffh;
    
    memset(&ffd, 0, sizeof ffd);
    ffd.dwFileAttributes = FILE_ATTRIBUTE_DIRECTORY;
    ffh = FindFirstFile(path, &ffd);
    if (ffh != INVALID_HANDLE_VALUE) {
        FindClose(ffh);
        return 1;
    } else {
        return 0;
    }
}

    
