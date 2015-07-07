package de.mpg.imeji.logic.jobs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * Clean unused {@link MetadataProfile}
 * 
 * @author saquet
 *
 */
public class CleanMetadataProfileJob implements Callable<Integer> {

  private boolean delete = false;
  private List<MetadataProfile> profiles = new ArrayList<MetadataProfile>();

  public CleanMetadataProfileJob(boolean delete) {
    this.delete = delete;
  }

  @Override
  public Integer call() throws Exception {
    Search s = new SPARQLSearch(SearchType.ALL, null);
    List<String> r =
        s.searchSimpleForQuery(SPARQLQueries.selectUnusedMetadataProfiles()).getResults();
    ProfileController pc = new ProfileController();
    for (String uri : r) {
      profiles.add(pc.retrieve(URI.create(uri), Imeji.adminUser));
    }
    if (delete) {
      for (MetadataProfile mdp : profiles) {
        pc.delete(mdp, Imeji.adminUser);
      }
    }
    return 1;
  }

  public List<MetadataProfile> getProfiles() {
    return profiles;
  }

}
