package org.opengis.cite.ogcapicoverages10;

import static org.opengis.cite.ogcapicoverages10.SuiteAttribute.IUT;
import static org.opengis.cite.ogcapicoverages10.SuiteAttribute.NO_OF_COLLECTIONS;
import static org.opengis.cite.ogcapicoverages10.SuiteAttribute.CONFORMANCECLASSES;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.reprezen.kaizen.oasparser.model3.OpenApi3;
import com.reprezen.kaizen.oasparser.OpenApiParser;
import org.opengis.cite.ogcapicoverages10.conformance.ConformanceClass;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class CommonDataFixture extends CommonFixture {

    private static final int DEFAULT_NUMBER_OF_COLLECTIONS = 3;

    private OpenApi3 apiModel;

    private List<ConformanceClass> conformanceClasses;

    protected int noOfCollections = DEFAULT_NUMBER_OF_COLLECTIONS;

    @BeforeClass
    public void conformanceClasses( ITestContext testContext ) {
        this.conformanceClasses = (List<ConformanceClass>) testContext.getSuite().getAttribute( CONFORMANCECLASSES.getName() );
    }

    @BeforeClass
    public void noOfCollections( ITestContext testContext ) {
        Object noOfCollections = testContext.getSuite().getAttribute( NO_OF_COLLECTIONS.getName() );
        if ( noOfCollections != null ) {
            this.noOfCollections = (Integer) noOfCollections;
        }
    }

    @BeforeClass
    public void retrieveApiModel( ITestContext testContext ) {
    	
    	
    	URI modelUri=null;
		try {
			modelUri = new URI(testContext.getSuite().getAttribute( IUT.getName() ).toString()+"/api");
		} catch (URISyntaxException e) {
		
			e.printStackTrace();
		}
		
    
    	boolean validate = false;

		modelUri = appendFormatToURI(modelUri);
		try {
			this.apiModel = (OpenApi3) new OpenApiParser().parse(modelUri.toURL(), validate);
		} catch (Exception ed) {
			try {
				modelUri = new URI(modelUri.toString().replace("application/json", "json"));

				this.apiModel = (OpenApi3) new OpenApiParser().parse(modelUri.toURL(), validate);
			} catch (Exception ignored) {
			}
		}
    	    	
      
    }
    private URI appendFormatToURI(URI input)
	{
		URI modelUri = null;
		try {

			if (input.toString().contains("?")) {
				modelUri = new URI(input.toString() + "f=application/json");
			} else {
				modelUri = new URI(input.toString() + "?f=application/json");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return modelUri;
	}
    public OpenApi3 getApiModel() {
        if ( apiModel == null )
            throw new SkipException( "ApiModel is not available." );
        return apiModel;
    }

    protected List<String> createListOfMediaTypesToSupportForOtherResources( Map<String, Object> linkToSelf ) {
        if ( this.conformanceClasses == null )
            throw new SkipException( "No requirement classes described in  resource /conformance available" );
        List<String> mediaTypesToSupport = new ArrayList<>();
        for ( ConformanceClass conformanceClass : this.conformanceClasses )
            if ( conformanceClass.hasMediaTypeForOtherResources() )
                mediaTypesToSupport.add( conformanceClass.getMediaTypeOtherResources() );
        if ( linkToSelf != null )
            mediaTypesToSupport.remove( linkToSelf.get( "type" ) );
        return mediaTypesToSupport;
    }

    protected List<String> createListOfMediaTypesToSupportForFeatureCollectionsAndFeatures() {
        if ( this.conformanceClasses == null )
            throw new SkipException( "No requirement classes described in  resource /conformance available" );
        List<String> mediaTypesToSupport = new ArrayList<>();
        for ( ConformanceClass requirementClass : this.conformanceClasses )
            if ( requirementClass.hasMediaTypeForFeaturesAndCollections() )
                mediaTypesToSupport.add( requirementClass.getMediaTypeFeaturesAndCollections() );
        return mediaTypesToSupport;
    }

    protected List<String> createListOfMediaTypesToSupportForFeatureCollectionsAndFeatures( Map<String, Object> linkToSelf ) {
        List<String> mediaTypesToSupport = createListOfMediaTypesToSupportForFeatureCollectionsAndFeatures();
        if ( linkToSelf != null )
            mediaTypesToSupport.remove( linkToSelf.get( "type" ) );
        return mediaTypesToSupport;
    }
}
