package clientserver;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PageHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.NameHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PNTypeHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.ArcHLAPI;
import fr.lip6.move.pnml.framework.general.PnmlExport;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.framework.utils.exception.VoidRepositoryException;
import fr.lip6.move.pnml.framework.utils.ModelRepository;
import java.io.File;
import java.io.IOException;

public class Generator1 {

	public void generate_file(int nbcli, int nbsv, int dim1, int dim2) {	
		
		Client1[] cl = new Client1[nbcli];
		Server1[] sv = new Server1[nbsv];
		ArcHLAPI[][] cli_sys = new ArcHLAPI[nbcli][4];
		ArcHLAPI[][][] sys_sv = new ArcHLAPI[nbcli][nbsv][2];
		
		try {
			ModelRepository.getInstance().createDocumentWorkspace("generator");
			PetriNetDocHLAPI doc = new PetriNetDocHLAPI();
			PetriNetHLAPI net = new PetriNetHLAPI("gen", PNTypeHLAPI.PTNET, new NameHLAPI("gen"), doc);
			PageHLAPI page = new PageHLAPI("toppage", new NameHLAPI("gen"), null, net);
			
			for (int i=0; i<nbcli; i++) {
				cl[i] = new Client1(i,page,(dim1/nbcli)/2+i*dim1/nbcli,dim2/4);
			}
			
			Channel1 sys = new Channel1(page,cl,dim1);

			for (int i=0; i<nbsv; i++) {
				sv[i] = new Server1(i,cl,page,(dim1/nbsv)/2+i*dim1/nbsv,dim2/2+50);
			}
			
			//set up arcs
			for (int i=0; i<nbcli; i++) {
				//between the channel and a client				
				cli_sys[i][0] = new ArcHLAPI("a0of"+i,cl[i].get_init(),sys.get_send(i) ,page);				
				cli_sys[i][1] = new ArcHLAPI("a1of"+i,sys.get_send(i) ,cl[i].get_att() ,page);
				cli_sys[i][2] = new ArcHLAPI("a2of"+i,cl[i].get_att() ,sys.get_rec(i)  ,page);
				cli_sys[i][3] = new ArcHLAPI("a3of"+i,sys.get_rec(i)  ,cl[i].get_cc(),page);
				//between the channel and a server
				
				for (int j=0; j<nbsv; j++) {
					sys_sv[i][j][0] = new ArcHLAPI("req_cl"+i+"sv"+j,sys.get_sysreq(i),sv[j].get_req(i) ,page);
					sys_sv[i][j][1] = new ArcHLAPI("rep_cl"+i+"sv"+j,sv[j].get_rep(i) ,sys.get_sysrep(i),page);
				}
			}
			//System.out.println("Everything generated");
			File dir = new File (System.getProperty("user.dir")+"/testmodel");
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					throw new IOException("Failed to create directory " + dir.getAbsolutePath());
				}
			}
			PnmlExport pex = new PnmlExport();
			pex.exportObject(doc,"testmodel/client"+nbcli+"_server"+nbsv+"_v1.pnml");
			System.out.println("File client"+nbcli+"_server"+nbsv+"_v1.pnml exported to testmodel directory.");
			ModelRepository.getInstance().destroyCurrentWorkspace();
		} catch (InvalidIDException e) {
			System.out.println("InvalidIDException caught by while running generator");
			e.printStackTrace();
		} catch (VoidRepositoryException e) {
			System.out.println("VoidRepositoryException caught by while running generator");
			e.printStackTrace();	
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

