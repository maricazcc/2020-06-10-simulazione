package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;


public class Simulazione {
	//CODA DEGLI EVENTI
	private Map <Integer, Actor> attoriIntervistati;
		
	//MODELLO DEL MONDO
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private List<Actor> listaAttori;
	
	//PARAMETRI DI INPUT
	private int giorni;	
		
	//PARAMETRI DI OUTPUT
	private int pause;
	
	
	public Simulazione(Graph<Actor, DefaultWeightedEdge> grafo, int n) {
		this.grafo = grafo;
		this.giorni = n;
	}

	public void init() {
		this.pause = 0;		
		this.attoriIntervistati = new HashMap<>();
		this.listaAttori = new ArrayList<>(this.grafo.vertexSet());	
	}
	
	public void run() {
		for(int i=1; i<=this.giorni; i++) {
			Random rand = new Random();
			
			//Se è il primo giorno oppure il giorno dopo una pausa			
			if(i==1 || !this.attoriIntervistati.containsKey(i-1)) {				
				Actor a = listaAttori.get(rand.nextInt(listaAttori.size()));
				this.attoriIntervistati.put(i, a);
				this.listaAttori.remove(a);
				continue;
			}
			
			//Se per 2 giorni consecutivi sono stati intervistati attori dello stesso sesso
			if(i>2 && this.attoriIntervistati.containsKey(i-1) && this.attoriIntervistati.containsKey(i-2)) {
				if(this.attoriIntervistati.get(i-1).getGender().equals(this.attoriIntervistati.get(i-2).getGender())) {
					if(rand.nextFloat() <= 0.9) {
						this.pause++;
						continue;
					}
				}
			}			
			
			//Dal secondo giorno in poi
			if(rand.nextFloat() <= 0.6) {
				Actor a = listaAttori.get(rand.nextInt(listaAttori.size()));
				this.attoriIntervistati.put(i, a);
				this.listaAttori.remove(a);
				continue;
			} else {
				Actor ultimoIntervistato = this.attoriIntervistati.get(i-1);
				Actor consigliato = this.getConsigliato(ultimoIntervistato);
				
				//Se l'ultimo attore non è in grado di fornire consigli oppure l'attore consigliato è stato già intervistato
				if(consigliato == null || !this.listaAttori.contains(consigliato)) {
					Actor a = listaAttori.get(rand.nextInt(listaAttori.size()));
					this.attoriIntervistati.put(i, a);
					this.listaAttori.remove(a);
					continue;
				} else {
					this.attoriIntervistati.put(i, consigliato);
					this.listaAttori.remove(consigliato);
					continue;
				}
				
			}
		}
		
	}

	private Actor getConsigliato(Actor ultimoIntervistato) {
		int peso = 0;
		Actor consigliato = null;
		
		for(Actor a : Graphs.neighborListOf(this.grafo, ultimoIntervistato)) {
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(ultimoIntervistato, a)) > peso) {
				consigliato = a;
				peso = (int) this.grafo.getEdgeWeight(this.grafo.getEdge(ultimoIntervistato, a));
			}
		}
		return consigliato;
	}

	public Collection <Actor> getAttoriIntervistati() {
		return attoriIntervistati.values();
	}

	public int getPause() {
		return pause;
	}
	
	

}
