package de.mpg.imeji.presentation.metadata;

import java.net.URI;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;

/**
 * Class wrapping all possible fields of a metadata. Necessary to display
 * metadata on the xhtnl page.
 * 
 * @author saquet
 * 
 */
public class SuperMetadataBean extends Metadata {

	private Metadata metadata;
	// All possible fields:
	private String text;
	private Person person;
	private URI coneId;
	private URI uri;
	private String label;
	private String date;
	private double longitude = Double.NaN;
	private double latitude = Double.NaN;
	private String name;
	private String exportFormat;
	private String citation;
	private double number = Double.NaN;
	private String license = null;

	public SuperMetadataBean(Metadata metadata) {
		this.metadata = metadata;
		metadata.getTypeNamespace();
		if (metadata instanceof Text) {
			text = ((Text) metadata).getText();
		} else if (metadata instanceof de.mpg.imeji.logic.vo.predefinedMetadata.Number) {
			number = ((de.mpg.imeji.logic.vo.predefinedMetadata.Number) metadata)
					.getNumber();
		} else if (metadata instanceof Link) {
			label = ((Link) metadata).getLabel();
			uri = ((Link) metadata).getUri();
		} else if (metadata instanceof Date) {
			date = ((Date) metadata).getDate();
		} else if (metadata instanceof ConePerson) {
			person = ((ConePerson) metadata).getPerson();
			coneId = ((ConePerson) metadata).getConeId();
		} else if (metadata instanceof License) {
			license = ((License) metadata).getLicense();
		} else if (metadata instanceof Publication) {
			citation = ((Publication) metadata).getCitation();
			uri = ((Publication) metadata).getUri();
		}
	}

	@Override
	public String getTypeNamespace() {
		return metadata.getTypeNamespace();
	}

	public void copy(Metadata metadata) {
		// TODO Auto-generated method stub
	}

	@Override
	public URI getStatement() {
		return metadata.getStatement();
	}

	@Override
	public void setStatement(URI namespace) {
		// TODO Auto-generated method stub
	}

	@Override
	public String asFulltext() {
		return metadata.asFulltext();
	}

	public Metadata getMetadata() {
		if (metadata instanceof Text) {
			 ((Text) metadata).setText(text);
		} else if (metadata instanceof de.mpg.imeji.logic.vo.predefinedMetadata.Number) {
			((de.mpg.imeji.logic.vo.predefinedMetadata.Number) metadata)
					.setNumber(number);
		} else if (metadata instanceof Link) {
			((Link) metadata).setLabel(label);
			((Link) metadata).setUri(uri);
		} else if (metadata instanceof Date) {
			((Date) metadata).setDate(date);
		} else if (metadata instanceof ConePerson) {
			 ((ConePerson) metadata).setPerson(person);
			 ((ConePerson) metadata).setConeId(coneId);
		} else if (metadata instanceof License) {
			 ((License) metadata).setLicense(license);
		} else if (metadata instanceof Publication) {
			 ((Publication) metadata).setCitation(citation);
			 ((Publication) metadata).setUri(uri);
		}
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public URI getConeId() {
		return coneId;
	}

	public void setConeId(URI coneId) {
		this.coneId = coneId;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(String exportFormat) {
		this.exportFormat = exportFormat;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

}
