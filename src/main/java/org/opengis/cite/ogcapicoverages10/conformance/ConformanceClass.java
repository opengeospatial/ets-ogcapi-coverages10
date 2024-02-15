package org.opengis.cite.ogcapicoverages10.conformance;

/**
 * 
 *
 * Encapsulates all known requirement classes.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public enum ConformanceClass {

    CORE( "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/core" ),
	GeodataCoverage("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geodata-coverage");



    private final String conformanceClass;

    private final String mediaTypeFeaturesAndCollections;

    private final String mediaTypeOtherResources;

    ConformanceClass( String conformanceClass ) {
        this( conformanceClass, null, null );
    }

    ConformanceClass( String conformanceClass, String mediaTypeFeaturesAndCollections, String mediaTypeOtherResources ) {
        this.conformanceClass = conformanceClass;
        this.mediaTypeFeaturesAndCollections = mediaTypeFeaturesAndCollections;
        this.mediaTypeOtherResources = mediaTypeOtherResources;
    }

    /**
     * @return <code>true</code> if the ConformanceClass has a media type for features and collections,
     *         <code>true</code> otherwise
     */
    public boolean hasMediaTypeForFeaturesAndCollections() {
        return mediaTypeFeaturesAndCollections != null;
    }

    /**
     * @return media type for features and collections, <code>null</code> if not available
     */
    public String getMediaTypeFeaturesAndCollections() {
        return mediaTypeFeaturesAndCollections;
    }

    /**
     * @return <code>true</code> if the ConformanceClass has a media type for other resources,
     *         <code>true</code> otherwise
     */
    public boolean hasMediaTypeForOtherResources() {
        return mediaTypeOtherResources != null;
    }

    /**
     * @return media type of other resources, <code>null</code> if not available
     */
    public String getMediaTypeOtherResources() {
        return mediaTypeOtherResources;
    }

    /**
     * @param conformanceClass
     *            the conformance class of the ConformanceClass to return.
     * @return the ConformanceClass with the passed conformance class, <code>null</code> if ConformanceClass exists
     */
    public static ConformanceClass byConformanceClass( String conformanceClass ) {
        for ( ConformanceClass requirementClass : values() ) {
            if ( requirementClass.conformanceClass.equals( conformanceClass ) )
                return requirementClass;
        }
        return null;
    }

}