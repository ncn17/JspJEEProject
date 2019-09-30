package cg.tpjee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cg.tpjee.beans.Utilisateur;
import cg.tpjee.proprietes.DAOException;

public class UtilisateurDAO extends DAO<Utilisateur> {

    public UtilisateurDAO( Connection conn ) {
        super( conn );
    }

    public final static String QUERY_FIND     = "SELECT * FROM Utilisateur WHERE email = ?";
    public final static String QUERY_FIND_ALL = "SELECT * FROM Utilisateur ";
    public final static String QUERY_CREATE   = "INSERT INTO Utilisateur (id,email, mot_de_passe, nom, date_inscription) VALUES (NULL,?, ?, ?, NOW())";

    @Override
    public Utilisateur find( int id ) throws DAOException {
        return null;
    }

    public Utilisateur find( String email ) throws DAOException {
        Utilisateur user = null;
        ResultSet resultat = null;
        PreparedStatement requete = null;

        try {
            requete = DAOUtils.buildQuery( this.conn, QUERY_FIND, DAOUtils.READ_QUERY );
            requete.setString( 1, email );
            resultat = requete.executeQuery();

            if ( resultat.first() ) {
                user = Hydrate( resultat );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOUtils.close( requete, resultat );
        }

        return user;
    }

    @Override
    public ArrayList<Utilisateur> findAll() throws DAOException {
        ArrayList<Utilisateur> userList = new ArrayList<Utilisateur>();
        ResultSet resultat = null;
        PreparedStatement requete = null;

        try {
            requete = DAOUtils.buildQuery( conn, QUERY_FIND_ALL, DAOUtils.READ_QUERY );
            resultat = requete.executeQuery();

            while ( resultat.next() ) {
                userList.add( Hydrate( resultat ) );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOUtils.close( requete, resultat );
        }

        return userList;
    }

    @Override
    public boolean create( Utilisateur user ) throws DAOException {
        PreparedStatement requete = null;
        boolean temoin = true;

        try {
            requete = DAOUtils.buildQuery( conn, QUERY_CREATE, DAOUtils.CD_QUERY );
            requete.setString( 1, user.getEmail() );
            requete.setString( 2, user.getMotDePasse() );
            requete.setString( 3, user.getNom() );

            int status = requete.executeUpdate();

            if ( status == 0 ) {
                temoin = false;
                throw new DAOException( "Echec de la création de l'utilisateur" );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOUtils.close( requete );
        }

        return temoin;
    }

    @Override
    public boolean update( Utilisateur obj ) throws DAOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delete( Utilisateur obj ) throws DAOException {
        // TODO Auto-generated method stub
        return false;
    }

    public Utilisateur Hydrate( ResultSet res ) throws SQLException {
        return new Utilisateur( res.getLong( "id" ),
                res.getString( "nom" ),
                res.getString( "email" ),
                res.getString( "mot_de_passe" ),
                res.getTimestamp( "date_inscription" ) );
    }

}
