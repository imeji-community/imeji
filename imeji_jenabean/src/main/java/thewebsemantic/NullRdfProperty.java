package thewebsemantic;

import java.lang.annotation.Annotation;

class NullRdfProperty implements RdfProperty {
	public boolean symmetric() {
		return false;
	}

	public boolean transitive() {
		return false;
	}

	public String value() {
		return "";
	}

	public Class<? extends Annotation> annotationType() {
		return null;
	}

	public String inverseOf() {
		return "";
	}
}