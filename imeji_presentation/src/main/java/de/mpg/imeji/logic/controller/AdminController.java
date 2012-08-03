/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import de.mpg.imeji.logic.vo.User;

public class AdminController extends ImejiController
{
    public AdminController(User user)
    {
        super(user);
    }

    public void cleanGraph()
    {
        // super.cleanGraph();
    }
}
