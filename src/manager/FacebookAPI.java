package manager;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.primefaces.context.RequestContext;

@Named
@SessionScoped
public class FacebookAPI implements Serializable {


	private static final long serialVersionUID = 3658300628580536494L;
	
	private String username;
    private String password;
	
	private SocialAuthManager socialManager;
	private Profile profile;

	private final String mainURL 		= "http://192.168.1.103:9091/EstacionamentoOnlineEntradaCliente/sistema.jsf";
	private final String redirectURL 	= "http://192.168.1.103:9091/EstacionamentoOnlineEntradaCliente/redirectHome.jsf";
	//private final String mainURL 		= "http://localhost:9091/EstacionamentoOnlineEntradaCliente/sistema.jsf";
	//private final String redirectURL 	= "http://localhost:9091/EstacionamentoOnlineEntradaCliente/redirectHome.jsf";
	private final String provider 		= "facebook";

	public void conectarFacebook() {
		Properties prop = System.getProperties();
		prop.put("graph.facebook.com.consumer_key", "1683062935316658");
		prop.put("graph.facebook.com.consumer_secret", "fd3969376367f68db05c00462e6370c7");
		prop.put("graph.facebook.com.custom_permissions", "public_profile, email, user_birthday");

		SocialAuthConfig socialConfig = SocialAuthConfig.getDefault();
		try {
			socialConfig.load(prop);
			socialManager = new SocialAuthManager();
			socialManager.setSocialAuthConfig(socialConfig);
			String URLRetorno = socialManager.getAuthenticationUrl(provider, redirectURL);
			FacesContext.getCurrentInstance().getExternalContext().redirect(URLRetorno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getPerfilUsuario() throws Exception {
		ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) ex.getRequest();

		Map<String, String> parametros = SocialAuthUtil.getRequestParametersMap(request);

		if (socialManager != null) {
			AuthProvider provider = socialManager.connect(parametros);
			this.setProfile(provider.getUserProfile());
		}

		FacesContext.getCurrentInstance().getExternalContext().redirect(mainURL);
	}
	
	public String conectar() {
		
		RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = null;
        boolean loggedIn = false;
         
        if(username != null && username.equals("admin") && password != null && password.equals("admin")) {
            loggedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bem vindo", username);
            return "/sistema.xhtml";
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aten��o", "Login ou senha inv�lidos");
        }
        
        FacesContext.getCurrentInstance().addMessage(null, message);
        context.addCallbackParam("loggedIn", loggedIn);
        
        return null;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
