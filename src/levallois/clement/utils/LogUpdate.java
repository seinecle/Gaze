/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levallois.clement.utils;

import Gaze.Screen_1;

/**
 *
 * @author C. Levallois
 */
public class LogUpdate {

    public static void update(String string) {
        Screen_1.log.setText(Screen_1.log.getText().concat(string).concat("\n"));
        Screen_1.log.setCaretPosition(Screen_1.log.getText().length());
    }
}
