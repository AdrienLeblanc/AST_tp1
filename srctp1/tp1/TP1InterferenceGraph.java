package tp1;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import rtl.Assign;
import rtl.Function;
import rtl.Ident;
import rtl.Liveness;
import rtl.Parser;
import rtl.Program;
import rtl.graph.AbstractInterferenceGraph;
import rtl.graph.RtlCFG;

public class TP1InterferenceGraph extends AbstractInterferenceGraph {


	public static void main(String[] args) {
		try {
			String path = "/Users/david/Works/svn/lande/teaching/Master1/AST/src/";
			java.io.InputStream in = new FileInputStream(path+"rtl/examples/ExInterference.rtl");
			Program prog = rtl.Parser.run(in);
			for (Function f: prog.functions) {
				System.out.println(f.headerToString());
				RtlCFG g = new RtlCFG(f);          // construction de controlf flow graph
				Liveness live = new Liveness(g);   // construction de l'analyse
				live.build();                      // de liveness avec la version enseignant
				TP1InterferenceGraph igraph = new TP1InterferenceGraph(g,live);
				igraph.show(System.out);
			}
		} catch (Throwable e) {
			System.out.println("Interference graph construction failed: " + e.getMessage());
		}

	}

	public TP1InterferenceGraph(RtlCFG g, Liveness live) {
		for (Node n : g.nodes()) {
			
		}
	}

	public Ident ident(Node n) { 
		return null; //TODO
	}


	// teste si l'object i est une instruction de la forme Assign(target,arg)
	private boolean isMove(Ident target, Ident arg, Object i) {
		if (!(i instanceof Assign)) return false;
		Assign asgn = (Assign) i;
		return asgn.ident.equals(target) && arg.equals(asgn.operand); 		
	}

}
