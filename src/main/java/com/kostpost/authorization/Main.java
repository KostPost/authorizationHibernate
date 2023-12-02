package com.kostpost.authorization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Provider;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kostpost.authorization.LogAccount.account;
import com.kostpost.authorization.LogAccount.accountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

public class Main {

    public static void main(String[] args) {


        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.OFF);
        Logger logger = Logger.getLogger("org.hibernate.SQL");
        logger.setLevel(Level.OFF);

        Scanner askAction = new Scanner(System.in);
        String action;

        accountRepository accountRepository = new accountRepository();
        do{
            System.out.println("1 - See all accounts\n2 - Create account\n3 - Log in account\n4 - Exit");
            action = askAction.nextLine();


            switch (action){

                case "1": {
                    List<account> allAcc = accountRepository.getAllData();

                    System.out.println("\n\n\n\nAll accounts:");
                    System.out.println("----------------------");

                    accountRepository.print(allAcc);

                    System.out.println("----------------------");

                    break;
                }

                case "2":{
                    Scanner askData = new Scanner(System.in);
                    String newAccountName;
                    String newAccountPassword;

                    boolean isExist = true;

                    do {
                        System.out.println("Enter a name for new account");
                        newAccountName = askData.nextLine();

                        isExist = checkIfNameExists(newAccountName);

                    }while (isExist);

                    System.out.println("Enter a password for new account");
                    newAccountPassword = askData.nextLine();

                    account newAccount = new account();
                    newAccount.setAccountName(newAccountName);
                    newAccount.setAccountPassword(newAccountPassword);

                    Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
                    SessionFactory sessionFactory = configuration.buildSessionFactory();

                    try (Session session = sessionFactory.openSession()) {
                        Transaction transaction = session.beginTransaction();

                        // Save the new user to the database
                        session.save(newAccount);

                        // Commit the transaction
                        transaction.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        sessionFactory.close();
                    }
                    break;
                }
                
                case "3":{
                    EntityManager entityManager = Persistence.createEntityManagerFactory("PostgreSQLDialect")
                            .createEntityManager();
                    Scanner askData = new Scanner(System.in);
                    String askName, askPassword = null;
                    
                    account checkAccount = null;
                    
                    do{
                        System.out.println("Enter a name account");
                        askName = askData.nextLine();

                        checkAccount = findAccountByName(entityManager,askName);
                        
                        if(checkAccount == null){
                            System.out.println("This account doesn't exist");
                        }

                    }while (checkAccount == null);
                    
                    do{
                        System.out.println("Enter a password for account: " + checkAccount.getAccountName());
                        askPassword = askData.nextLine();
                        
                        if(!Objects.equals(askPassword, checkAccount.getAccountPassword())){
                            System.out.println("Wrong password");
                        }

                    }while (!Objects.equals(askPassword, checkAccount.getAccountPassword()));
                    
                    System.out.println("You successfully log in");
  

                    
                }


            }


        }while (!action.equals("4"));


    }

    public static account findAccountByName(EntityManager entityManager, String accountName) {
        try {
            TypedQuery<account> query = entityManager.createQuery(
                    "SELECT a FROM account a WHERE a.accountName = :name", account.class);
            query.setParameter("name", accountName);
            query.setMaxResults(1); // Получить только один результат
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkIfNameExists(String nameToCheck) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT COUNT(*) FROM accounts WHERE account_name = :name";
            NativeQuery<?> query = session.createNativeQuery(sql);
            query.setParameter("name", nameToCheck);
            
            Number count = (Number) query.uniqueResult();

            return count != null && count.intValue() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
