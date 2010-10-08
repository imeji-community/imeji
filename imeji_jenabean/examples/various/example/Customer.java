package example;

import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import thewebsemantic.Bean2RDF;
import thewebsemantic.Id;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;

public class Customer {
	
	@Id int customerId;
	String name;

	public Customer() {}
	
	public Customer(int id) {
		customerId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public static void main(String[] args) {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		Customer cust = new Customer(0);
		cust.setName("1st National");
		writer.save(cust);
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		m.setNsPrefix("", "http://example/");
		m.write(System.out, "N3");
		
		RDF2Bean reader = new RDF2Bean(m);
		Collection<Customer> customers = reader.load(Customer.class);
		
		//Customer cust = reader.load(Customer.class, 0);
		//Sparql.exec(m, c, query);
	}

}


