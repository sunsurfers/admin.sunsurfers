package com.vaadin.demo.sunadmin;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;


public class DBconnector {

    public static void create(){

        EntityManager entityManager = Persistence
                .createEntityManagerFactory("sunsurf")
                .createEntityManager();

    }
}
