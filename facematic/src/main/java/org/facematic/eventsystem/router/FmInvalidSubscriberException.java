package org.facematic.eventsystem.router;

import org.facematic.core.annotations.FmViewComponent;

/**
 * Do throw this exception when your subscriber not consistent now.<br>
 * Example:<br>
 * <pre>
 * public class SomeController {
 *    ...
 *   &#64;FmViewComponent
 *   VerticalLayout view
 * 
 *   public void handleEvent (SomeEvent ev) {<br>
 *      if (!VaadinUtil.isViewAlive (view)) {
 *          throw new FmInvalidSubscriberException ();
 *      }
 *      ...
 *   }
 *   ...
 * 
 * }
 * </pre>
 * @author stas
 *
 */
public class FmInvalidSubscriberException extends RuntimeException {
}
