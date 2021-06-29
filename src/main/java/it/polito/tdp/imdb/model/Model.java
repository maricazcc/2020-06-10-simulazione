package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	private ImdbDAO dao;
	private Graph<Actor, DefaultWeightedEdge>grafo;
	private Map<Integer, Actor> idMap;
	
	public Model() {
		this.dao = new ImdbDAO();
	}
	
	public List<String> getGenres() {
		return this.dao.listAllGenres();
	}
	
	public String creaGrafo(String genre) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		
		//Aggiungo vertici
		this.dao.getVertici(genre, idMap);
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungo archi
		List<Adiacenza> archi = this.dao.getAdiacenze(genre, idMap);
		for(Adiacenza a : archi)
		Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		
		return String.format("Grafo creato con %d vertici e %d archi.\n", 
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}
	
	public List<Actor> listaVertici() {
		List<Actor> result = new ArrayList<>();
		
	    for(Actor a: this.grafo.vertexSet())
	    	result.add(a);
	    
	    Collections.sort(result);
	    return result;
	}

	public Graph<Actor, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	public List<Actor> trovaAttoriSimili(Actor attore) {
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(grafo);
		List<Actor> result = new ArrayList<>(ci.connectedSetOf(attore));
		result.remove(attore);
		
		Collections.sort(result);
		return result;		
	}
	
	public String simula(int n) {
		String result = "";
		Simulazione s = new Simulazione(this.grafo, n);
		s.init();
		s.run();
		
		result+="Gli attori intervistati in " + n +" giorni sono:\n";
		
		for(Actor a : s.getAttoriIntervistati())
			result+=a.toString() + "\n";
		
		result+="Le pause prese dal produttore sono: " + s.getPause();		
		return result;
	}

}
