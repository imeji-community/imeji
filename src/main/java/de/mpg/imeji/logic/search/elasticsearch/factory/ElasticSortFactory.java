package de.mpg.imeji.logic.search.elasticsearch.factory;

import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import de.mpg.imeji.logic.search.elasticsearch.model.ElasticFields;
import de.mpg.imeji.logic.search.model.SearchIndex.SearchFields;
import de.mpg.imeji.logic.search.model.SortCriterion;

/**
 * Factory for {@link SortBuilder}
 * 
 * @author bastiens
 * 
 */
public class ElasticSortFactory {

  private static SortBuilder defaultSort = SortBuilders.fieldSort(ElasticFields.CREATED.field())
      .order(SortOrder.ASC);

  /**
   * Build a {@link SortBuilder} from a {@link SortCriterion}
   * 
   * @param sort
   * @return
   */
  public static SortBuilder build(SortCriterion sort) {
    if (sort == null || sort.getIndex() == null) {
      return defaultSort;
    }
    SearchFields index = SearchFields.valueOf(sort.getIndex().getName());
    switch (index) {
      case text:
        return SortBuilders.fieldSort(ElasticFields.METADATA_TEXT.field())
            .order(getSortOrder(sort));
      case date:
        return SortBuilders.fieldSort(ElasticFields.METADATA_TEXT.field())
            .order(getSortOrder(sort));
      case modified:
        return SortBuilders.fieldSort(ElasticFields.MODIFIED.field()).order(getSortOrder(sort));
      default:
        return defaultSort;
    }
  }

  /**
   * REturn the {@link SortOrder} of the current sort criterion
   * 
   * @param sort
   * @return
   */
  private static SortOrder getSortOrder(SortCriterion sort) {
    return sort.getSortOrder() == de.mpg.imeji.logic.search.model.SortCriterion.SortOrder.ASCENDING ? SortOrder.ASC
        : SortOrder.DESC;
  }
}
