package cg.ncn.JspJEE.forms;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import cg.ncn.JspJEE.beans.Client;
import cg.ncn.JspJEE.outils.BoxOutils;
import cg.ncn.JspJEE.outils.Props;
import eu.medsea.mimeutil.MimeUtil;

@MultipartConfig
public class InscriptionForm {

    private static final String CHAMP_NOM     = "nomClient";
    private static final String CHAMP_PRENOM  = "prenomClient";
    private static final String CHAMP_ADRESSE = "adresseClient";
    private static final String CHAMP_TEL     = "telephoneClient";
    private static final String CHAMP_MAIL    = "emailClient";
    private static final String CHAMP_IMAGE   = "imageClient";

    private String              resultat;
    private Map<String, String> erreurs       = new HashMap<String, String>();

    public Client inscrireClient( HttpServletRequest request ) throws IOException {
        Client client = new Client();

        String nom = BoxOutils.getChamp( request, CHAMP_NOM );
        String prenom = BoxOutils.getChamp( request, CHAMP_PRENOM );
        String adresse = BoxOutils.getChamp( request, CHAMP_ADRESSE );
        String tel = BoxOutils.getChamp( request, CHAMP_TEL );
        String mail = BoxOutils.getChamp( request, CHAMP_MAIL );
        String fileName = "";

        try {
            BoxOutils.verifyTexte( nom, 2, "le  nom doit contneir aumoins 2 caractères." );
        } catch ( Exception e ) {
            setErreurs( CHAMP_NOM, e.getMessage() );
        }

        try {
            BoxOutils.verifyTexte( prenom, 2, "le  prénom doit contenir aumoins 2 caractères." );
        } catch ( Exception e ) {
            setErreurs( CHAMP_PRENOM, e.getMessage() );
        }

        try {
            BoxOutils.verifyTexte( adresse, 10, "L'adresse doit contenir aumoins 10 caractères." );
        } catch ( Exception e ) {
            setErreurs( CHAMP_ADRESSE, e.getMessage() );
        }

        try {
            BoxOutils.verifyNumero( tel );
        } catch ( Exception e ) {
            setErreurs( CHAMP_TEL, e.getMessage() );
        }

        try {
            BoxOutils.verifyMail( mail );
        } catch ( Exception e ) {
            setErreurs( CHAMP_MAIL, e.getMessage() );
        }

        // traitement de l'image
        if ( erreurs.isEmpty() ) {

            InputStream dataFile = null;

            try {
                Part part = request.getPart( CHAMP_IMAGE );

                if ( part != null ) {
                    /* vérification si type champ File */
                    fileName = BoxOutils.getNomFichier( part );
                }
                // test type file
                if ( !fileName.isEmpty() && fileName != null ) {
                    /* champ file confirmer extraction nom Simple fix IE bug */
                    fileName = fileName.substring( fileName.lastIndexOf( '/' ) + 1 )
                            .substring( fileName.lastIndexOf( '\\' ) + 1 );

                    // make a new name for count
                    String ext = fileName.substring( fileName.lastIndexOf( '.' ) + 1 );

                    fileName = Props.PREFIX_IMG + BoxOutils.countClient( request ) + '.' + ext;

                    /* get data file */
                    dataFile = part.getInputStream();
                }
            } catch ( IllegalStateException e ) {
                /* fichier trop volumineux */
                e.printStackTrace();
                setErreurs( CHAMP_IMAGE, "Fichier trop lourd : Limite 1Mo" );
            } catch ( IOException e ) {
                /* erreur niveau serveur */
                e.printStackTrace();
                setErreurs( CHAMP_IMAGE, "Oops une erreur interne ! veuillez réessayer plus tard" );
            } catch ( ServletException e ) {
                /* erreur niveau requete formulaire corrompue */
                e.printStackTrace();
                setErreurs( CHAMP_IMAGE,
                        "Ce type de requête n'est pas supporté, merci d'utiliser le formulaire prévu pour envoyer votre fichier." );
            }

            // stockage de l'image
            /* pas d'erreur validation fileName */
            if ( erreurs.isEmpty() ) {
                try {
                    BoxOutils.validationFichier( fileName, dataFile );
                } catch ( Exception e ) {
                    setErreurs( CHAMP_IMAGE, e.getMessage() );
                }
                /*
                 * verification du mime du fichier depuis le stream du fichier
                 */
                MimeUtil.registerMimeDetector( "eu.medsea.mimeutil.detector.MagicMimeMimeDetector" );
                Collection<?> mimeTypes = MimeUtil.getMimeTypes( dataFile );
                /*
                 * MIME must start by "image"
                 */
                if ( mimeTypes.toString().startsWith( "image" ) ) {
                    /* Écriture du fichier sur le disque */
                    try {
                        BoxOutils.writeFile( dataFile, fileName, Props.FILE_PATH );
                    } catch ( Exception e ) {
                        setErreurs( CHAMP_IMAGE,
                                "Erreur lors de l'écriture du fichier sur le disque." + e.getMessage() );
                    }
                } else {
                    setErreurs( CHAMP_IMAGE, "Veuillez selectionner une image valide.( .jpg , .jpeg)" );
                }
            }
        }
        // image zone
        // Map<String, String> tmp_erreurs = SaveFile.save( request,
        // CHAMP_IMAGE, fileName );
        //
        // for ( Map.Entry<String, String> listError : tmp_erreurs.entrySet() )
        // {
        // setErreurs( listError.getKey(), listError.getValue() );
        // }

        if ( erreurs.isEmpty() ) {
            resultat = "Succès de la création du client";
            // assignation don't forget to add image name
            client = new Client( nom, prenom, adresse, tel, mail, fileName );
        } else {
            resultat = "Echec de la création du client";
        }

        return client;
    }

    public String getResultat() {
        return resultat;
    }

    public Map<String, String> getErreurs() {
        return erreurs;
    }

    public void setErreurs( String champ, String err ) {
        this.erreurs.put( champ, err );
    }

}
