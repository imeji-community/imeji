/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/album")
@j2jModel("album")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Album extends Container
{
}
