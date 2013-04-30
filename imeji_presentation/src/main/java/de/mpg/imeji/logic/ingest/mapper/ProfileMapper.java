/**
 * 
 */
package de.mpg.imeji.logic.ingest.mapper;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.ingest.template.DuplicatedObject;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * @author hnguyen
 */
public class ProfileMapper
{
    private DuplicatedObject<MetadataProfile, ?> dupProfiles;

    /**
     * @throws URISyntaxException
     */
    public ProfileMapper(List<MetadataProfile> profileList)
    {
        this.dupProfiles = this.process(profileList);
    }

    private DuplicatedObject<MetadataProfile, ?> process(List<MetadataProfile> profileList)
    {
    	DuplicatedObject<MetadataProfile, ?> dupProfiles = new DuplicatedObject<MetadataProfile, Object>();
        for (MetadataProfile profile : profileList)
        {
            MetadataProfile profileAsFilename = dupProfiles.getHashTableFilename().get(profile.getTitle());
            if (profileAsFilename == null)
            {
                dupProfiles.getHashTableFilename().put(new String(profile.getTitle()), profile);
            }
            else
            {
                dupProfiles.getDuplicateFilenames().add(new String(profile.getTitle()));
            }
        }
        return dupProfiles;
    }

    public List<String> getDuplicateFilenames()
    {
        return this.dupProfiles.getDuplicateFilenames();
    }

    public boolean hasDuplicateFilenames()
    {
        return !this.getDuplicateFilenames().isEmpty();
    }

    private Collection<MetadataProfile> getUniqueFilenameListsAsProfileList()
    {
        return this.dupProfiles.getHashTableFilename().values();
    }

    private Collection<String> getUniqueFilenameListsAsStringList()
    {
        return this.dupProfiles.getHashTableFilename().keySet();
    }

    public Collection<MetadataProfile> getMappedProfileObjects()
    {
        return this.getUniqueFilenameListsAsProfileList();
    }

    public Collection<String> getMappedProfileKeys()
    {
        return this.getUniqueFilenameListsAsStringList();
    }
}
