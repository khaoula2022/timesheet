package tn.esprit.spring.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import tn.esprit.spring.entities.Contrat;
import tn.esprit.spring.entities.Employe;
import tn.esprit.spring.entities.Entreprise;
import tn.esprit.spring.entities.Mission;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.entities.Timesheet;
import tn.esprit.spring.repository.ContratRepository;
import tn.esprit.spring.repository.EmployeRepository;
import tn.esprit.spring.services.IEmployeService;
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
	private static final Logger logger= LogManager.getLogger(TimesheetApplicationTests.class);*/


@Scope(value = "session")
@Controller(value = "employeController")
@ELBeanName(value = "employeController")
@Join(path = "/", to = "/login.jsf")
public class ControllerEmployeImpl  {

	@Autowired
	IEmployeService employeService;

    	
	@Autowired
	EmployeRepository employeRepository;
	
	@Autowired
	ContratRepository contratRepository;
	private static final Logger l = LogManager.getLogger(ControllerEmployeImpl.class);


	private String login; 
	private String password; 
	private Boolean loggedIn;
	private String response = "/login.xhtml?faces-redirect=true";

	private Employe authenticatedUser = null; 
	private String prenom; 
	private String nom; 
	private String email;
	private boolean actif;
	private Role role;  
	public Role[] getRoles() { return Role.values(); }

	private List<Employe> employes; 

	private Integer employeIdToBeUpdated; // getter et setter


	public String doLogin() {

		String navigateTo = "null";
		authenticatedUser=employeService.authenticate(login, password);
		if (authenticatedUser != null && authenticatedUser.getRole() == Role.ADMINISTRATEUR) {
			navigateTo = "/pages/admin/welcome.xhtml?faces-redirect=true";
			loggedIn = true;
		}		

		else
		{
			
			FacesMessage facesMessage =
					new FacesMessage("Login Failed: Please check your username/password and try again.");
			FacesContext.getCurrentInstance().addMessage("form:btn",facesMessage);
		}
		return navigateTo;	
	}

	public String doLogout()
	{
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	
	return this.response ;
	}


	public String addEmploye() {

		if (authenticatedUser==null || !loggedIn) return response;

		employeService.addOrUpdateEmploye(new Employe( actif , nom, prenom, email, password, role)); 
		return "null"; 
	}  

	public String removeEmploye(int employeId) {
		String navigateTo = "null";
		if (authenticatedUser==null || !loggedIn) return response;

		employeService.deleteEmployeById(employeId);
		return navigateTo; 
	} 

	public String displayEmploye(Employe empl) 
	{
		String navigateTo = "null";
		if (authenticatedUser==null || !loggedIn) return response;


		this.setPrenom(empl.getPrenom());
		this.setNom(empl.getNom());
		this.setActif(empl.isActif()); 
		this.setEmail(empl.getEmail());
		this.setRole(empl.getRole());
		this.setPassword(empl.getPassword());
		this.setEmployeIdToBeUpdated(empl.getId());

		return navigateTo; 

	} 

	public String updateEmploye() 
	{ 
		String navigateTo = "null";
		
		if (authenticatedUser==null || !loggedIn) return response;

		employeService.addOrUpdateEmploye(new Employe(employeIdToBeUpdated, nom, prenom, email, password, actif, role)); 

		return navigateTo; 

	} 


	// getters and setters 

	public IEmployeService getEmployeService() {
		return employeService;
	}

	public void setEmployeService(IEmployeService employeService) {
		this.employeService = employeService;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public List<Employe> getAllEmployes() {
		return employeService.getAllEmployes();
	}

	public Boolean getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public int ajouterEmploye(Employe employe)
	{
		employeService.addOrUpdateEmploye(employe);
		return employe.getId();
	}

	public void mettreAjourEmailByEmployeId(String email, int employeId) {
		employeService.mettreAjourPasswordByEmployeId(email, employeId);

	}

	public void affecterEmployeADepartement(int employeId, int depId) {
		employeService.affecterEmployeADepartement(employeId, depId);

	}



	public void desaffecterEmployeDuDepartement(int employeId, int depId)
	{
		employeService.desaffecterEmployeDuDepartement(employeId, depId);
	}

	public int ajouterContrat(Contrat contrat) {
		l.info("add contract ");
		employeService.ajouterContrat(contrat);
		return contrat.getReference();
	}

	public void affecterContratAEmploye(int contratId, int employeId)
	{
		l.info("affect employ a contract ");
		l.info("je chercher l'empoyer ");
		Optional<Employe> emp = employeRepository.findById(employeId);
		Optional<Contrat> con= contratRepository.findById(contratId);
		if(emp.isPresent() && con.isPresent()) {
			employeService.affecterContratAEmploye(contratId, employeId);

		}
		else {
			l.info("something wrong  ");

		}
	}


	public String getEmployePrenomById(int employeId) {
		return employeService.getEmployePrenomById(employeId);
	}

	public void deleteEmployeById(int employeId) {
		employeService.deleteEmployeById(employeId);

	}
	public void deleteContratById(int contratId) {
		l.info("delete employer ");
		Optional<Contrat> con= contratRepository.findById(contratId);
		if(con.isPresent()) {
			l.info("delete contract success");
			employeService.deleteContratById(contratId);

		}
		
		else {
			l.info("something wrong");

		}
	}

	public int getNombreEmployeJPQL() {

		return employeService.getNombreEmployeJPQL();
	}

	public List<String> getAllEmployeNamesJPQL() {

		return employeService.getAllEmployeNamesJPQL();
	}

	public List<Employe> getAllEmployeByEntreprise(Entreprise entreprise) {
		return employeService.getAllEmployeByEntreprise(entreprise);
	}

	public void mettreAjourEmailByEmployeIdJPQL(String email, int employeId) {	
		employeService.mettreAjourEmailByEmployeIdJPQL(email, employeId);

	}

	public void deleteAllContratJPQL() {
		l.info("delete all contract ");
		employeService.deleteAllContratJPQL();
	}

	public float getSalaireByEmployeIdJPQL(int employeId) {
		return employeService.getSalaireByEmployeIdJPQL(employeId);
	}


	public Double getSalaireMoyenByDepartementId(int departementId) {
		return employeService.getSalaireMoyenByDepartementId(departementId);
	}

	public List<Timesheet> getTimesheetsByMissionAndDate(Employe employe, Mission mission, Date dateDebut,
			Date dateFin) {
		return employeService.getTimesheetsByMissionAndDate(employe, mission, dateDebut, dateFin);
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}




	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Employe> getEmployes() {
		employes = employeService.getAllEmployes(); 
		return employes;
	}

	public void setEmployes(List<Employe> employes) {
		this.employes = employes;
	}

	public Integer getEmployeIdToBeUpdated() {
		return employeIdToBeUpdated;
	}

	public void setEmployeIdToBeUpdated(Integer employeIdToBeUpdated) {
		this.employeIdToBeUpdated = employeIdToBeUpdated;
	}

	public Employe getAuthenticatedUser() {
		return authenticatedUser;
	}

	public void setAuthenticatedUser(Employe authenticatedUser) {
		this.authenticatedUser = authenticatedUser;
	}

}
