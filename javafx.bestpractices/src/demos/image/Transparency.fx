package image;

import javafx.ui.*;
import javafx.ui.canvas.*;

var x : Number = 40;
    
var img : Image = Image { url : "resources/overlay.png" };
Frame {
    content : Canvas {
        content : [
            ImageView {
                image : Image { url : "resources/background.png" }
            },
            ImageView {
                image : bind img
                transform : [ Translate { x : bind x, y : 100 - 32 }, Scale { x : 0.5, y : 0.5 } ]
                opacity : 0.5
            }
        ]
        onMouseMoved : function( e : MouseEvent ):Void {
            x = e.x - 100 * 0.5;
        }
    }
    
    visible : true
    title : "Transparency"
    width : 200
    height : 232
    onClose : function() { java.lang.System.exit( 0 ); }
}