package uk.ac.open.kmi.watson.services.dist;

import uk.ac.open.kmi.watson.services.EntityResult;
import uk.ac.open.kmi.watson.services.SearchConf;

public class WatsonService {
	
	protected WatsonService() {	}
	
	protected uk.ac.open.kmi.watson.clientapi.SearchConf tranformConfFromServerToClient(SearchConf conf){
		uk.ac.open.kmi.watson.clientapi.SearchConf conf2 = new uk.ac.open.kmi.watson.clientapi.SearchConf();
    	conf2.setEntities(conf.getEntities());
    	conf2.setEntitiesInfo(conf.getEntitiesInfo());
    	conf2.setFilters(conf.getFilters());
    	conf2.setInc(conf.getInc());
    	conf2.setMatch(conf.getMatch());
    	conf2.setRankingWeights(conf.getRankingWeights());
    	conf2.setSCInfo(conf.getSCInfo());
    	conf2.setScope(conf.getScope());
    	conf2.setSorts(conf.getSorts());
    	conf2.setStart(conf.getStart());
    	return conf2;
	}
	
	
	EntityResult[] transformEntityResultArrayFromClientToServer(uk.ac.open.kmi.watson.clientapi.EntityResult[] res){
		EntityResult[] res2 = new EntityResult[res.length];
		for (int i =0; i < res.length; i++){
			res2[i] = transformEntityResultFromClientToServer(res[i]);
		}
		return res2;
	}

	private EntityResult transformEntityResultFromClientToServer(uk.ac.open.kmi.watson.clientapi.EntityResult er) {
		EntityResult er2 = new EntityResult(er.getURI());
		er2.setComment(er.getComment());
		er2.setLabels(er.getLabels());
		er2.setLiterals(er.getLiterals());
		er2.setRelationFrom(er.getRelationFrom());
		er2.setRelationTo(er.getRelationTo());
		er2.setScore(er.getScore());
		er2.setSCURI(er.getSCURI());
		er2.setType(er.getType());
		return er2;
	}
}
