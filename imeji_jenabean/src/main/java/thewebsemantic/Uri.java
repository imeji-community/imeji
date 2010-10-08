package thewebsemantic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * &#064;Uri indicates which property (via its getter method) 
 * provides the beans URI.  Use this annotation when you want to 
 * handle beans in a more RDF centric way.  This requires you
 * to construct your own uri's in the method annoated with &#064;Uri.
 * 
 * Normally beans using this pattern will take a single string as the
 * uri in their constructor.
 * 
 * <code>
 * 
 * public class ExampleBean {
 * 
 *   private String uri;
 *   
 *   public ExampleBean(String uri) {
 *     this.uri = uri;
 *   }
 *   
 *   &#064;Uri public uri() {
 *     return this.uri;
 *   }
 *   
 *   // other properties follow
 * 
 * }
 * </code>
 * @deprecated use combination of java.net.URI and {@link #Id} annotation instead
 */
@Deprecated
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) 
public @interface Uri {

}
/*
	Copyright (c) 2007 
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/