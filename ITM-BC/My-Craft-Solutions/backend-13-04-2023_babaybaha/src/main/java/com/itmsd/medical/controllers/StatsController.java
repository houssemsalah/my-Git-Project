package com.itmsd.medical.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import com.itmsd.medical.services.RdvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.itmsd.medical.entities.Soudeur;
import com.itmsd.medical.payload.request.Personne;
import com.itmsd.medical.repositories.PlombierRepository;
import com.itmsd.medical.services.DateAttribute;


@RestController
@RequestMapping("stats")
public class StatsController {
	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private UserRepository userRepository;
	private RendezVousRepository rendezVousRepository;
	private ClientRepository clientRepository;	
	private MenuisierRepository MenuisierRepository;	
	private ElectricienRepository electricienRepository;
	private PlombierRepository plombierRepository;
	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;	
	private PeintreRepository peintreRepository;	
	private SoudeurRepository soudeurRepository;
	private RdvService rendezVousService;
	
	@Autowired
	public StatsController(SerrurierRepository serrurierRepository ,ChauffagisteRepository chauffagisteRepository,UserRepository userRepository, RendezVousRepository rendezVousRepository,
						   ClientRepository clientRepository, MenuisierRepository MenuisierRepository,
						   ElectricienRepository electricienRepository, CarreleurRepository carreleurRepository , PlombierRepository plombierRepository,
						   PaysagisteRepository paysagisteRepository, PeintreRepository peintreRepository,
						   SoudeurRepository soudeurRepository , RdvService rendezVousService) {
		super();
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.userRepository = userRepository;
		this.rendezVousRepository = rendezVousRepository;
		this.clientRepository = clientRepository;
		this.MenuisierRepository = MenuisierRepository;
		this.electricienRepository = electricienRepository;
		this.carreleurRepository=carreleurRepository;
		this.plombierRepository = plombierRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.rendezVousService=rendezVousService;
	}
	
	@GetMapping("/info")
	public Stats getStats() {
		Stats stats = new Stats();
		//list praticiens
		//nb praticiens = prat - nb deleted
		List<User> users = userRepository.findAll();
		stats.nbTotalUsers = users.size();
		List<Client> clients = clientRepository.findAll();
		stats.nbClients = clients.size();
		stats.nbChauffagiste = chauffagisteRepository.findAll().size();
		stats.nbSerrurier = serrurierRepository.findAll().size();
		stats.nbMenuisier = MenuisierRepository.findAll().size();
		stats.nbCarreleur = carreleurRepository.findAll().size();
		stats.nbPlombier = plombierRepository.findAll().size();
		stats.nbPaysagiste = paysagisteRepository.findAll().size();
		stats.nbSoudeur = soudeurRepository.findAll().size();
		stats.nbElectricien = electricienRepository.findAll().size();
		stats.nbPeintre = peintreRepository.findAll().size();
		stats.nbPraticiens = stats.nbPeintre + stats.nbElectricien + stats.nbSoudeur +
				stats.nbPaysagiste + stats.nbPlombier + stats.nbMenuisier+stats.nbCarreleur+stats.nbChauffagiste+stats.nbSerrurier;
		stats.nbTotalRdv = rendezVousRepository.findAll().size();
		
		rendezVousRepository.findAll().forEach(rdv -> {
			if (rdv.getStatus().equals("en cours")) {
				stats.nbPendingRdv++;
			}else if (rdv.getStatus().equals("accepté")) {
				stats.nbConfirmedRdv++;
			}else if (rdv.getStatus().equals("refusé")) {
				stats.nbRefusedRdv++;
			}
		});
		if (!users.isEmpty()) {
			users.forEach(usr ->{
				if (usr.getOnline().contains("online")) {
					stats.nbOnlineUsers++;
				}
			});
		}
		
		List<Client> clientsList = clientRepository.findAll();
		
		clientsList.forEach(client -> {
			Date d2 = null;
			try {
				d2 = DateAttribute.getDate(client.getCreatedAt());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cp = Calendar.getInstance();
			cp.setTime(d2);
			Date currentDate = new Date();
			Calendar dd = Calendar.getInstance();
			dd.setTime(currentDate);
		
			if (cp.get(Calendar.YEAR) == dd.get(Calendar.YEAR)) {
				if (cp.get(Calendar.MONTH) == 0) stats.newClients.set(0, stats.newClients.get(0)+1);
				if (cp.get(Calendar.MONTH) == 1) stats.newClients.set(1, stats.newClients.get(1)+1);
				if (cp.get(Calendar.MONTH) == 2) stats.newClients.set(2, stats.newClients.get(2)+1) ;
				if (cp.get(Calendar.MONTH) == 3) stats.newClients.set(3, stats.newClients.get(3)+1);
				if (cp.get(Calendar.MONTH) == 4) stats.newClients.set(4, stats.newClients.get(4)+1);
				if (cp.get(Calendar.MONTH) == 5) stats.newClients.set(5, stats.newClients.get(5)+1);
				if (cp.get(Calendar.MONTH) == 6) stats.newClients.set(6, stats.newClients.get(6)+1);
				if (cp.get(Calendar.MONTH) == 7) stats.newClients.set(7, stats.newClients.get(7)+1);
				if (cp.get(Calendar.MONTH) == 8) stats.newClients.set(8, stats.newClients.get(8)+1);
				if (cp.get(Calendar.MONTH) == 9) stats.newClients.set(9, stats.newClients.get(9)+1);
				if (cp.get(Calendar.MONTH) == 10) stats.newClients.set(10, stats.newClients.get(10)+1);
				if (cp.get(Calendar.MONTH) == 11) stats.newClients.set(11, stats.newClients.get(11)+1);					
			}
		});
		
		List<RendezVous> rdvsList = rendezVousRepository.findAll();
		rdvsList.forEach(rdv -> {
			Date d2 = null;
			try {
				d2 = DateAttribute.getDate(rdv.getCreatedAt());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cp = Calendar.getInstance();
			cp.setTime(d2);
			Date currentDate = new Date();
			Calendar dd = Calendar.getInstance();
			dd.setTime(currentDate);
		
			if (cp.get(Calendar.YEAR) == dd.get(Calendar.YEAR)) {
				if (cp.get(Calendar.MONTH) == 0) stats.newRdv.set(0, stats.newRdv.get(0)+1);
				if (cp.get(Calendar.MONTH) == 1) stats.newRdv.set(1, stats.newRdv.get(1)+1);
				if (cp.get(Calendar.MONTH) == 2) stats.newRdv.set(2, stats.newRdv.get(2)+1) ;
				if (cp.get(Calendar.MONTH) == 3) stats.newRdv.set(3, stats.newRdv.get(3)+1);
				if (cp.get(Calendar.MONTH) == 4) stats.newRdv.set(4, stats.newRdv.get(4)+1);
				if (cp.get(Calendar.MONTH) == 5) stats.newRdv.set(5, stats.newRdv.get(5)+1);
				if (cp.get(Calendar.MONTH) == 6) stats.newRdv.set(6, stats.newRdv.get(6)+1);
				if (cp.get(Calendar.MONTH) == 7) stats.newRdv.set(7, stats.newRdv.get(7)+1);
				if (cp.get(Calendar.MONTH) == 8) stats.newRdv.set(8, stats.newRdv.get(8)+1);
				if (cp.get(Calendar.MONTH) == 9) stats.newRdv.set(9, stats.newRdv.get(9)+1);
				if (cp.get(Calendar.MONTH) == 10) stats.newRdv.set(10, stats.newRdv.get(10)+1);
				if (cp.get(Calendar.MONTH) == 11) stats.newRdv.set(11, stats.newRdv.get(11)+1);					
			}
		});
		if (MenuisierRepository.count()>0) {
		List<Menuisier> topMenuisier = MenuisierRepository.findTopMenuisier();
		stats.topMenuisier = new Personne();
		stats.topMenuisier.id = topMenuisier.get(0).getId();
		stats.topMenuisier.firstName = topMenuisier.get(0).getFirstName();
		stats.topMenuisier.lastName = topMenuisier.get(0).getLastName();
			stats.topMenuisier.lastName = topMenuisier.get(0).getLastName();
			stats.topMenuisier.lastName = topMenuisier.get(0).getLastName();
		stats.topMenuisier.createdAt= topMenuisier.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topMenuisier.id);
			topMenuisier.get(0).setNbRdv(rdvs.size());
			MenuisierRepository.save(topMenuisier.get(0));
			stats.topMenuisier.nbRdv= topMenuisier.get(0).getNbRdv();
		stats.topMenuisier.status= topMenuisier.get(0).getOnline();
		}
		if (carreleurRepository.count()>0) {
			List<Carreleur> topCarreleur = carreleurRepository.findTopPraticien();
			stats.topCarreleur = new Personne();
			stats.topCarreleur.id = topCarreleur.get(0).getId();
			stats.topCarreleur.firstName = topCarreleur.get(0).getFirstName();
			stats.topCarreleur.lastName = topCarreleur.get(0).getLastName();
			stats.topCarreleur.lastName = topCarreleur.get(0).getLastName();
			stats.topCarreleur.lastName = topCarreleur.get(0).getLastName();
			stats.topCarreleur.createdAt= topCarreleur.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topCarreleur.id);
			topCarreleur.get(0).setNbRdv(rdvs.size());
			carreleurRepository.save(topCarreleur.get(0));
			stats.topCarreleur.nbRdv= topCarreleur.get(0).getNbRdv();
			stats.topCarreleur.status= topCarreleur.get(0).getOnline();
		}
		if (soudeurRepository.count()>0) {
		List<Soudeur> topSoudeur = soudeurRepository.findTopPraticien();
			stats.topSoudeur = new Personne();
			stats.topSoudeur.id = topSoudeur.get(0).getId();
			stats.topSoudeur.firstName = topSoudeur.get(0).getFirstName();
			stats.topSoudeur.lastName = topSoudeur.get(0).getLastName();
			stats.topSoudeur.createdAt= topSoudeur.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topSoudeur.id);
			topSoudeur.get(0).setNbRdv(rdvs.size());
			soudeurRepository.save(topSoudeur.get(0));
			stats.topSoudeur.nbRdv= topSoudeur.get(0).getNbRdv();
			stats.topSoudeur.status= topSoudeur.get(0).getOnline();
		}
		if (paysagisteRepository.count()>0) {
		List<Paysagiste> topPaysagiste = paysagisteRepository.findTopPraticien();
		stats.topPaysagiste = new Personne();
		stats.topPaysagiste.id = topPaysagiste.get(0).getId();
		stats.topPaysagiste.firstName = topPaysagiste.get(0).getFirstName();
			stats.topPaysagiste.lastName = topPaysagiste.get(0).getLastName();
		stats.topPaysagiste.createdAt= topPaysagiste.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topPaysagiste.id);
			topPaysagiste.get(0).setNbRdv(rdvs.size());
			paysagisteRepository.save(topPaysagiste.get(0));
			stats.topPaysagiste.nbRdv= topPaysagiste.get(0).getNbRdv();
		stats.topPaysagiste.status= topPaysagiste.get(0).getOnline();
		}
		if (serrurierRepository.count()>0) {
			List<Serrurier> topSerrurier = serrurierRepository.findTopSerrurier();
			stats.topSerrurier = new Personne();
			stats.topSerrurier.id = topSerrurier.get(0).getId();
			stats.topSerrurier.firstName = topSerrurier.get(0).getFirstName();
			stats.topSerrurier.lastName = topSerrurier.get(0).getLastName();
			stats.topSerrurier.createdAt= topSerrurier.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topSerrurier.id);
			topSerrurier.get(0).setNbRdv(rdvs.size());
			serrurierRepository.save(topSerrurier.get(0));
			stats.topSerrurier.nbRdv= topSerrurier.get(0).getNbRdv();
			stats.topSerrurier.status= topSerrurier.get(0).getOnline();
		}
		if (chauffagisteRepository.count()>0) {
			List<Chauffagiste> topChauffagiste = chauffagisteRepository.findTopChauffagiste();
			stats.topChauffagiste = new Personne();
			stats.topChauffagiste.id = topChauffagiste.get(0).getId();
			stats.topChauffagiste.firstName = topChauffagiste.get(0).getFirstName();
			stats.topChauffagiste.lastName = topChauffagiste.get(0).getLastName();
			stats.topChauffagiste.createdAt= topChauffagiste.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topChauffagiste.id);
			topChauffagiste.get(0).setNbRdv(rdvs.size());
			chauffagisteRepository.save(topChauffagiste.get(0));
			stats.topChauffagiste.nbRdv= topChauffagiste.get(0).getNbRdv();
			stats.topChauffagiste.status= topChauffagiste.get(0).getOnline();
		}
		if (plombierRepository.count()>0) {
		List<Plombier> topPlombier= plombierRepository.findTopPlombier();
		stats.topPlombier = new Personne();
		stats.topPlombier.id = topPlombier.get(0).getId();
		stats.topPlombier.firstName = topPlombier.get(0).getFirstName();
		stats.topPlombier.lastName = topPlombier.get(0).getLastName();
		stats.topPlombier.createdAt= topPlombier.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topPlombier.id);
			topPlombier.get(0).setNbRdv(rdvs.size());
			plombierRepository.save(topPlombier.get(0));
		stats.topPlombier.nbRdv= topPlombier.get(0).getNbRdv();
		stats.topPlombier.status= topPlombier.get(0).getOnline();
		}

		if (peintreRepository.count()>0) {
		List<Peintre> topPeintre = peintreRepository.findTopPraticien();
		stats.topPeintre = new Personne();
		stats.topPeintre.id = topPeintre.get(0).getId();
		stats.topPeintre.firstName = topPeintre.get(0).getFirstName();
		stats.topPeintre.lastName = topPeintre.get(0).getLastName();
		stats.topPeintre.createdAt= topPeintre.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topPeintre.id);
			topPeintre.get(0).setNbRdv(rdvs.size());
			peintreRepository.save(topPeintre.get(0));
			stats.topPeintre.nbRdv= topPeintre.get(0).getNbRdv();
		stats.topPeintre.status= topPeintre.get(0).getOnline();
		}
		if (electricienRepository.count()>0) {
		List<Electricien> topElectricien = electricienRepository.findTopPraticien();
		stats.topElectricien = new Personne();
		stats.topElectricien.id = topElectricien.get(0).getId();
		stats.topElectricien.firstName = topElectricien.get(0).getFirstName();
		stats.topElectricien.lastName = topElectricien.get(0).getLastName();
		stats.topElectricien.createdAt= topElectricien.get(0).getCreatedAt();
			List <RendezVous> rdvs = new ArrayList<>();
			rdvs = rendezVousService.getAllRendezVousperso(stats.topElectricien.id);
			topElectricien.get(0).setNbRdv(rdvs.size());
			electricienRepository.save(topElectricien.get(0));
			stats.topElectricien.nbRdv= topElectricien.get(0).getNbRdv();
		stats.topElectricien.status= topElectricien.get(0).getOnline();
		}
		return stats;
	}

	public class Stats {
		public long nbVisitors;
		public int nbTotalUsers;
		public int nbClients;
		public int nbOnlineUsers;
		
		public int nbPraticiens;
		public int nbMenuisier;
		public int nbCarreleur;
		public int nbPlombier;
		public int nbSoudeur;
		public int nbPeintre;
		public int nbElectricien;
		public int nbPaysagiste;
		public int nbChauffagiste;
		public int nbSerrurier;
		public int nbTotalRdv;
		public int nbConfirmedRdv;
		public int nbRefusedRdv;
		public int nbPendingRdv;
		
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topMenuisier;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topSoudeur;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topCarreleur;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topPlombier;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topPaysagiste;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topPeintre;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topChauffagiste;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topSerrurier;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topElectricien;
		
		public ArrayList<Integer> newClients = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));
		public ArrayList<Integer> newRdv = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));


		public Stats(long nbVisitors, int nbTotalUsers, int nbPraticiens, int nbClients, int nbOnlineUsers,
				int nbTotalRdv,int nbChauffagiste,int nbSerrurier, int nbMenuisier, int nbCarreleur, int nbPlombier, int nbSoudeur, int nbPeintre, int nbElectricien,
				int nbPaysagiste, int nbConfirmedRdv, int nbRefusedRdv, int nbPendingRdv, Personne topMenuisier,
				Personne topSoudeur,Personne topCarreleur, Personne topPlombier, Personne topPaysagiste, Personne topPeintre,
				Personne topElectricien, ArrayList<Integer> newClients, ArrayList<Integer> newRdv) {
			super();
			this.nbChauffagiste=nbChauffagiste;
			this.nbSerrurier=nbSerrurier;
			this.nbVisitors = nbVisitors;
			this.nbTotalUsers = nbTotalUsers;
			this.nbPraticiens = nbPraticiens;
			this.nbClients = nbClients;
			this.nbOnlineUsers = nbOnlineUsers;
			this.nbTotalRdv = nbTotalRdv;
			this.nbMenuisier = nbMenuisier;
			this.nbCarreleur=nbCarreleur;
			this.nbPlombier = nbPlombier;
			this.nbSoudeur = nbSoudeur;
			this.nbPeintre = nbPeintre;
			this.nbElectricien = nbElectricien;
			this.nbPaysagiste = nbPaysagiste;
			this.nbConfirmedRdv = nbConfirmedRdv;
			this.nbRefusedRdv = nbRefusedRdv;
			this.nbPendingRdv = nbPendingRdv;
			this.topMenuisier = topMenuisier;
			this.topSoudeur = topSoudeur;
			this.topCarreleur=topCarreleur;
			this.topPlombier = topPlombier;
			this.topPaysagiste = topPaysagiste;
			this.topPeintre = topPeintre;
			this.topElectricien = topElectricien;
			this.newClients = newClients;
			this.newRdv = newRdv;
		}

		public Stats() {
			super();
			// TODO Auto-generated constructor stub
		}

	}

}
