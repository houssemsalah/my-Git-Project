package com.itmsd.medical.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.itmsd.medical.entities.*;
import com.itmsd.medical.entities.Plombier;

@JsonInclude(Include.NON_NULL)
public class SearchResponse {
	private List<Serrurier> serruriers;
	private List<Chauffagiste> chauffagistes;
	private List<Paysagiste> paysagistes;
	private List<Soudeur> soudeurs;
	private List<Plombier> plombiers;
	private List<Carreleur> carreleurs;
	private List<Peintre> peintres;
	private List<Electricien> electriciens;
	private List<Menuisier> Menuisiers;
	private List<Object> all;

	public List<Object> getAll() {
		return all;
	}

	public void setAll(List<Object> all) {
		this.all = all;
	}

	public List<Serrurier> getSerruriers() {
		return serruriers;
	}

	public void setSerruriers(List<Serrurier> serruriers) {
		this.serruriers = serruriers;
	}

	public List<Chauffagiste> getChauffagistes() {
		return chauffagistes;
	}

	public void setChauffagistes(List<Chauffagiste> chauffagistes) {
		this.chauffagistes = chauffagistes;
	}

	public List<Carreleur> getCarreleurs() {
		return carreleurs;
	}
	public void setCarreleurs(List<Carreleur> carreleurs) {
		this.carreleurs = carreleurs;
	}
	public List<Paysagiste> getPaysagistes() {
		return paysagistes;
	}
	public void setPaysagistes(List<Paysagiste> paysagistes) {
		this.paysagistes = paysagistes;
	}
	public List<Soudeur> getSoudeurs() {
		return soudeurs;
	}
	public void setSoudeurs(List<Soudeur> soudeurs) {
		this.soudeurs = soudeurs;
	}
	public List<Plombier> getPlombiers() {
		return plombiers;
	}
	public void setPlombiers(List<Plombier> plombiers) {
		this.plombiers = plombiers;
	}
	public List<Peintre> getPeintres() {
		return peintres;
	}
	public void setPeintres(List<Peintre> peintres) {
		this.peintres = peintres;
	}
	public List<Electricien> getElectriciens() {
		return electriciens;
	}
	public void setElectriciens(List<Electricien> electriciens) {
		this.electriciens = electriciens;
	}
	public List<Menuisier> getMenuisiers() {
		return Menuisiers;
	}
	public void setMenuisiers(List<Menuisier> Menuisiers) {
		this.Menuisiers = Menuisiers;
	}
	
}
