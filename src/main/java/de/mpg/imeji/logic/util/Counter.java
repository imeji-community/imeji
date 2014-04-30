/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.util;

import java.net.URI;

import de.mpg.imeji.logic.Imeji;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/counter")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Counter
{
    private URI id = Imeji.counterID;
    @j2jLiteral("http://imeji.org/terms/counterValue")
    private int counter;

    public void setCounter(int counter)
    {
        this.counter = counter;
    }

    public int getCounter()
    {
        return counter;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }
}
