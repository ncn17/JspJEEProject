package cg.tpjee.dao;

import java.sql.Connection;

import cg.tpjee.bdd.BDDMysql;
import cg.tpjee.beans.Client;
import cg.tpjee.beans.Commande;
import cg.tpjee.beans.Utilisateur;

public class DAOFactory {

    /* appel direct et unique de la bdd instenciation jvm */
    protected static Connection conn = BDDMysql.getConnexion();

    public DAO<Utilisateur> getUilisateurDAO() {
        return new UtilisateurDAO( conn );
    }

    public static DAO<Client> getClientDAO() {
        return new ClientDAO( conn );
    }

    public static DAO<Commande> getCommandeDAO() {
        return new CommandeDAO( conn );
    }

}
