package tp1;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rtl.*;
import rtl.graph.FlowGraph;
import rtl.graph.RtlCFG;
import rtl.graph.Graph.Node;
import rtl.interpreter.ErrorException;

public class TP1Liveness {


	public static void main(String[] args) {
		try {
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/BinaryTree.rtl");// A MODIFIER
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/BubbleSort.rtl");// A MODIFIER
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/ExLiveness.rtl");// A MODIFIER
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/ExLiveness2.rtl");// A MODIFIER
			java.io.InputStream in = new FileInputStream("C:/Users/Administrateur/workspace/AST_tp1/srctp1/rtl/examples/Factorial.rtl");// A MODIFIER
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/LinearSearch.rtl");// A MODIFIER
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/LinkedList.rtl");// A MODIFIER
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/MoreThan4.rtl");// A MODIFIER
			//			java.io.InputStream in = new FileInputStream("/private/student/2/52/17000952/workspace/AST_tp1/srctp1/rtl/examples/QuickSort.rtl");// A MODIFIER
			Program prog = rtl.Parser.run(in);
			for (Function f: prog.functions) {
				System.out.println(f.headerToString());
				FlowGraph g = new RtlCFG(f);
				LivenessDebug debug = new LivenessDebug(g);
				TP1Liveness live = new TP1Liveness(g,debug);
				live.build();
				debug.show(System.out);
			}
		} catch (Throwable e) {
			System.out.println("Liveness analysis failed: " + e.getMessage());
		}
	}

	private Map<Node,Set<Ident>> liveIn = new Hashtable<Node,Set<Ident>>();
	private Map<Node,Set<Ident>> liveOut = new Hashtable<Node,Set<Ident>>();
	private Map<Node,Set<Ident>> liveInTmp = new Hashtable<Node,Set<Ident>>();
	private Map<Node,Set<Ident>> liveOutTmp = new Hashtable<Node,Set<Ident>>();

	private final LivenessDebug debug;
	private FlowGraph g;

	public TP1Liveness(FlowGraph g, LivenessDebug debug) {
		this.g = g;
		this.debug = debug;
	}

	public void build() {
		for (Node n : this.g.nodes()) { 
			this.liveIn.put(n, new HashSet<Ident>());
			this.liveOut.put(n, new HashSet<Ident>()); 
		}
		onePass(this.g);
		while (!isFixedPoint(this.g)) 
			onePass(this.g);		
	}

	public void onePass(FlowGraph g) {

		this.liveInTmp = new Hashtable<Node,Set<Ident>>();
		this.liveOutTmp = new Hashtable<Node,Set<Ident>>();

		for (Node n : this.liveIn.keySet()) {
			HashSet<Ident> set = new HashSet<Ident>();
			for (Ident i : this.liveIn(n)) {
				set.add(i);
			}
			this.liveInTmp.put(n, set);
		}
		
		for (Node n : this.liveOut.keySet()) {
			HashSet<Ident> set = new HashSet<Ident>();
			for (Ident i : this.liveOut(n)) {
				set.add(i);
			}
			this.liveOutTmp.put(n, set);
		}

		for (Node n : g.nodes()) {
			// VIVANTESe (l) = (VIVANTESs (l) MOINS def (l)) UNION use (l)
			this.liveIn.get(n).addAll(this.liveOut.get(n));
			this.liveIn.get(n).removeAll(g.def(n));
			this.liveIn.get(n).addAll(g.use(n));
			// VIVANTESs (l) = ENSEMBLE DE TOUTES LES VIVANTESe (l') QUI SUCCEDENT
			for (Node nSuivant : n.succ()) {
				this.liveOut.get(n).addAll(this.liveIn.get(nSuivant));
			}
		}

		if (this.debug!=null) this.debug.recordCurrentLivenessValues(this.liveIn, this.liveOut);
	}

	public boolean isFixedPoint(FlowGraph g) {
		boolean finish = true;
		for (Node n : g.nodes()) {
			finish = finish && this.liveIn.get(n).equals(this.liveInTmp.get(n)) && this.liveOut.get(n).equals(this.liveOutTmp.get(n));
		}
		return finish;
	}

	public Set<Ident> liveIn(Node n) {
		return this.liveIn.get(n);
	}

	public Set<Ident> liveOut(Node n) {
		return this.liveOut.get(n);
	}

}
