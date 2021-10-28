package main;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class mySession {

    private static SessionFactory sessionFactory;

    static public org.hibernate.Session getOpenSession(){

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
        org.hibernate.Session session = sessionFactory.openSession();

        return session;
    }

    static public void closeSessionFactory(){

        sessionFactory.close();
    }


}
