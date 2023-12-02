package com.kostpost.authorization.LogAccount;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class accountRepository {

    private static final SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(account.class)
                .buildSessionFactory();
    }

    public List<account> getAllData() {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            List<account> clients = session.createQuery("from account ", account.class).getResultList();

            session.getTransaction().commit();
            return clients;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    public void print(account account){
        System.out.println("ID: " + account.getId() + "\nAccount name: " + account.getAccountName()
                + "\tAccount password: " + account.getAccountPassword() + "\nDate created: " + account.getAccountDate());
    }

    public void print(List<account> account){
        if(account.size() != 0) {
            for (int i = 0; i < account.size(); i++) {
                System.out.println("ID: " + account.get(i).getId() + "\nAccount name: " + account.get(i).getAccountName()
                        + "\tAccount password: " + account.get(i).getAccountPassword() + "\nDate created: " + account.get(i).getAccountDate());
                System.out.println("\n");
            }
        }
    }







}
