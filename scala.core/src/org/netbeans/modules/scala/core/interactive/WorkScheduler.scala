package org.netbeans.modules.scala.core.interactive

import scala.collection.mutable.Queue

class WorkScheduler {

  type Action = () => Unit

  private var todo = new Queue[Action]
  private var except: Option[Exception] = None

  /** Called from server: block until todo list is nonempty */
  def waitForMoreWork() = synchronized {
    while (todo.isEmpty) { wait() }
  }

  /** called from Server: test whether todo list is nonempty */
  def moreWork(): Boolean = synchronized {
    todo.nonEmpty
  }

  /** Called from server: get first action in todo list, and pop it off */
  def nextWorkItem(): Option[Action] = synchronized {
    if (!todo.isEmpty) {
      Some(todo.dequeue())
    } else None
  }

  /** Called from server: return optional exception posted by client
   *  Reset to no exception.
   */
  def pollException(): Option[Exception] = synchronized {
    val result = except; except = None; result
  }

  /** Called from client: have action executed by server */
  def postWorkItem(action: Action) = synchronized {
    todo enqueue action
    notify()
  }

  /** Called from client: cancel all queued actions */
  def cancelQueued() = synchronized {
    todo.clear()
  }

  /** Called from client:
   *  Require an exception to be thrown on next poll.
   *
   * @Note: In interactive.Global, the `newRunnerThread` always waits for `scheduler.waitForMoreWork()`
   * before `pollForWork()`, which may cause raised `except`s never have chance to be polled, if
   * there is no more `WorkItem` in `todo` queue, so I have to post another Action to awake it.
   * @Ticket #2289
   */
  def raise(exc: Exception) = synchronized {
    except = Some(exc)
    postWorkItem {() => println("A action to awake scheduler to process "  + exc.getClass.getSimpleName + " except")}
  }
}

