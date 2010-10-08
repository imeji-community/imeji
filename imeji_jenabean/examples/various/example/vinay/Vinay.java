package example.vinay;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Vinay {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EntityManagerFactory factory =  Persistence.createEntityManagerFactory("tws:filemodel");
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Employee e = new Employee();
		e.setFistName("Vinay");
		e.setLastName("Patel");
		e.setRole(Role.ARCHITECT);
		em.persist(e);
		em.getTransaction().commit();
		
		
		
		Query q = em.createNamedQuery("Employee.ALL");
		q.setParameter("role", Role.ARCHITECT);
		Collection<Employee> emps = q.getResultList();
		for (Employee employee : emps) {			
			System.out.println(employee.getClass());
		}
	}

}
