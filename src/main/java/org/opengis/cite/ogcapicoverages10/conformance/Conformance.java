package org.opengis.cite.ogcapicoverages10.conformance;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.GET;
import static org.opengis.cite.ogcapicoverages10.SuiteAttribute.API_MODEL;
import static org.opengis.cite.ogcapicoverages10.SuiteAttribute.IUT;
import static org.opengis.cite.ogcapicoverages10.SuiteAttribute.CONFORMANCECLASSES;
import static org.opengis.cite.ogcapicoverages10.conformance.ConformanceClass.CORE;
import static org.opengis.cite.ogcapicoverages10.openapi3.OpenApiUtils.retrieveTestPointsForConformance;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.opengis.cite.ogcapicoverages10.CommonFixture;
import org.opengis.cite.ogcapicoverages10.openapi3.TestPoint;
import org.opengis.cite.ogcapicoverages10.openapi3.UriBuilder;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.reprezen.kaizen.oasparser.model3.MediaType;
import com.reprezen.kaizen.oasparser.model3.OpenApi3;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 *
 * A.?.?. Conformance Path {root}/conformance
 *
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class Conformance extends CommonFixture {

    private List<ConformanceClass> conformanceClasses;

    @DataProvider(name = "conformanceUris")
    public Object[][] conformanceUris( ITestContext testContext ) {
        OpenApi3 apiModel = (OpenApi3) testContext.getSuite().getAttribute( API_MODEL.getName() );
        URI iut = (URI) testContext.getSuite().getAttribute( IUT.getName() );

        TestPoint tp = new TestPoint(rootUri.toString(),"/conformance",null);


        List<TestPoint> testPoints = new ArrayList<TestPoint>();
        testPoints.add(tp);
        Object[][] testPointsData = new Object[1][];
        int i = 0;
        for ( TestPoint testPoint : testPoints ) {
            testPointsData[i++] = new Object[] { testPoint };
        }
        return testPointsData;
    }

    @AfterClass
    public void storeConformanceClassesInTestContext( ITestContext testContext ) {
        testContext.getSuite().setAttribute( CONFORMANCECLASSES.getName(), this.conformanceClasses );
    }

    /**
     * Partly addresses Requirement 1 : /req/tiles/core/conformance-success
     *
     * @param testPoint
     *            the test point to test, never <code>null</code>
     */
    @Test(description = "Implements A.?.?. Conformance Path {root}/conformance,", groups = "conformance", dataProvider = "conformanceUris")
    public void validateConformanceOperationAndResponse( TestPoint testPoint ) {
        String testPointUri = new UriBuilder( testPoint ).buildUrl();
        Response response = init().baseUri( testPointUri ).accept( JSON ).when().request( GET );
        validateConformanceOperationResponse( testPointUri, response );
    }

    /**
     * Requirement 1 : /req/tiles/core/conformance-success
     *
     * Abstract Test ?: /ats/core/conformance-success
     */
    private void validateConformanceOperationResponse( String testPointUri, Response response ) {
        response.then().statusCode( 200 );

        JsonPath jsonPath = response.jsonPath();
        this.conformanceClasses = parseAndValidateConformanceClasses( jsonPath );
        assertTrue( this.conformanceClasses.contains( CORE ),
                    "Conformance class \"http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/core\" is not available from path "
                                                              + testPointUri );
    }

    /**
     * @param jsonPath
     *            never <code>null</code>
     * @return the parsed requirement classes, never <code>null</code>
     * @throws AssertionError
     *             if the json does not follow the expected structure
     */
    List<ConformanceClass> parseAndValidateConformanceClasses( JsonPath jsonPath ) {
        List<Object> conformsTo = jsonPath.getList( "conformsTo" );
        assertNotNull( conformsTo, "Missing member 'conformsTo'." );

        List<ConformanceClass> conformanceClasses = new ArrayList<>();
        for ( Object conformTo : conformsTo ) {
       
            if ( conformTo instanceof String ) {
                String conformanceClassString = (String) conformTo;
                ConformanceClass conformanceClass = ConformanceClass.byConformanceClass( conformanceClassString );
                if ( conformanceClass != null )
                    conformanceClasses.add( conformanceClass );
            } else
                throw new AssertionError( "At least one element array 'conformsTo' is not a string value (" + conformTo
                                          + ")" );
        }
        return conformanceClasses;
    }

}
