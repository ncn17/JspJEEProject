package cg.ncn.JspJEE.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cg.ncn.JspJEE.beans.Client;
import cg.ncn.JspJEE.beans.Commande;
import cg.ncn.JspJEE.forms.CreerCommande;
import cg.ncn.JspJEE.outils.BoxOutils;
import cg.ncn.JspJEE.outils.Props;

@WebServlet( "/createCommande" )
public class CreateCommande extends HttpServlet {
    private static final long   serialVersionUID = 1L;
    private static final String CREATE           = "/WEB-INF/jsp/createCommande.jsp";
    private static final String SHOW             = "/listeCommande";
    private static final String ATT_CLIENT       = "commande";
    private static final String ATT_FORM         = "form";

    public CreateCommande() {
    }

    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher( CREATE ).forward( request, response );
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        // creation de client vide pour le cas d'un client existant
        Client client = null;
        String champ_old = request.getParameter( "Clients" );

        if ( champ_old != null && !champ_old.isEmpty() ) {
            int id = Integer.parseInt( champ_old );
            // recuperation client
            client = getClient( request, id );
        }

        CreerCommande form = new CreerCommande();

        Commande commande = form.creerCommande( request, client );

        request.setAttribute( ATT_CLIENT, commande );
        request.setAttribute( ATT_FORM, form );

        if ( form.getErreurs().isEmpty() ) {
            // add the new commande
            BoxOutils.addCommande( request, commande );
            // redirect to vue page
            response.sendRedirect( request.getContextPath() + SHOW );
        } else {
            doGet( request, response );
        }
    }

    @SuppressWarnings( "unchecked" )
    public Client getClient( HttpServletRequest req, int id ) {
        Client client = null;
        // get session
        HttpSession session = req.getSession();
        Map<Integer, Client> liste = (Map<Integer, Client>) session.getAttribute( Props.BDD_CLIENT );

        for ( Entry<Integer, Client> entry : liste.entrySet() ) {
            if ( entry.getKey() == id ) {
                client = entry.getValue();
            }
        }
        return client;
    }

}