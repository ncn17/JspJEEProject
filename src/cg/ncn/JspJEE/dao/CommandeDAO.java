package cg.tpjee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.joda.time.DateTime;

import cg.tpjee.beans.Client;
import cg.tpjee.beans.Commande;
import cg.tpjee.forms.ValidationForm;
import cg.tpjee.proprietes.DAOException;

public class CommandeDAO extends DAO<Commande> {

    public CommandeDAO( Connection conn ) {
        super( conn );
    }

    public final static String QUERY_DELETE   = "DELETE FROM Commande WHERE id = ?";
    public final static String QUERY_FIND     = "SELECT * FROM Commande WHERE id = ?";
    public final static String QUERY_FIND_ALL = "SELECT * FROM Commande ";
    public final static String QUERY_COUNT    = "SELECT MAX(id) FROM Commande ";
    public final static String QUERY_CREATE   = "INSERT INTO Commande VALUES(?,?,?,?,?,?,?,?) ";

    @Override
    public Commande find( int id ) throws DAOException {
        Commande commande = null;
        ResultSet resultat = null;
        PreparedStatement requete = null;

        try {
            requete = DAOUtils.buildQuery( this.conn, QUERY_FIND, DAOUtils.READ_QUERY );
            requete.setInt( 1, id );
            resultat = requete.executeQuery();

            if ( resultat.first() ) {
                commande = Hydrate( resultat );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOUtils.close( requete, resultat );
        }

        return commande;
    }

    @Override
    public ArrayList<Commande> findAll() throws DAOException {
        ArrayList<Commande> commandeList = new ArrayList<Commande>();
        ResultSet resultat = null;
        PreparedStatement requete = null;

        try {
            requete = DAOUtils.buildQuery( conn, QUERY_FIND_ALL, DAOUtils.READ_QUERY );
            resultat = requete.executeQuery();

            while ( resultat.next() ) {

                commandeList.add( Hydrate( resultat ) );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOUtils.close( requete, resultat );
        }

        return commandeList;
    }

    @Override
    public boolean create( Commande cmd ) throws DAOException {
        PreparedStatement requete = null;
        boolean temoin = true;

        try {

            Long nbre = 0L;
            ResultSet result = DAOUtils.buildQuery( this.conn, QUERY_COUNT, DAOUtils.READ_QUERY ).executeQuery();
            if ( result.first() ) {
                nbre = result.getLong( 1 );
            }
            cmd.setId( ++nbre );

            requete = DAOUtils.buildQuery( conn, QUERY_CREATE, DAOUtils.UPDATE_QUERY );
            requete.setLong( 1, cmd.getId() );
            requete.setLong( 2, cmd.getClient().getId() );
            requete.setTimestamp( 3, new Timestamp( cmd.getDate().getMillis() ) );
            requete.setString( 4, cmd.getModePayement() );
            requete.setString( 5, cmd.getStatusPayement() );
            requete.setString( 6, cmd.getModeLivraison() );
            requete.setString( 7, cmd.getStatusLivraison() );
            requete.setDouble( 8, cmd.getMontant() );

            int status = requete.executeUpdate();

            if ( status == 0 ) {
                temoin = false;
                throw new DAOException( "Echec de la création de la commande" );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOUtils.close( requete );
        }

        return temoin;
    }

    @Override
    public boolean update( Commande obj ) throws DAOException {
        return false;
    }

    @Override
    public boolean delete( Commande cmd ) throws DAOException {
        ResultSet resultat = null;
        PreparedStatement requete = null;
        Boolean temoin = true;

        try {
            requete = DAOUtils.buildQuery( this.conn, QUERY_DELETE, DAOUtils.READ_QUERY );
            requete.setLong( 1, cmd.getId() );

            temoin = requete.executeUpdate() > 0 ? true : false;

            if ( !temoin ) {
                throw new DAOException( "Echec de la suppréssion de la commande" );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            DAOUtils.close( requete, resultat );
        }

        return temoin;
    }

    @Override
    public Commande Hydrate( ResultSet res ) throws SQLException {

        /* recuperation du client de la commmande */
        Client client = DAOFactory.getClientDAO().find( res.getInt( "id_client" ) );

        /* recuperation date de création */
        DateTime date = null;
        try {
            date = ValidationForm.stringToDate( res.getTimestamp( "date_creation" ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return new Commande( res.getLong( "id" ),
                client,
                date,
                res.getString( "modePayement" ),
                res.getString( "statusPayement" ),
                res.getString( "modeLivraison" ),
                res.getString( "statusLivraison" ),
                res.getDouble( "montant" ) );

    }

}
