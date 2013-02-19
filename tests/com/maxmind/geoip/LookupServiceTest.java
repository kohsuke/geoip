package com.maxmind.geoip;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class LookupServiceTest {

  private static final File GEO_IP_DATABASE = new File("/usr/local/share/GeoIP/GeoIPCity.dat");
  private LookupService lookupService;

  @Before
  public void initialise() throws Exception {
    lookupService = new LookupService(GEO_IP_DATABASE);
  }

  @Test
  public void getLocationDoesNotThrowArrayIndexOutOfBoundsExceptionWhenCountryCodeNameNotPresent() {
    Location location = lookupService.getLocation("62.56.242.3");
    assertNotNull(location);
    assertNull(location.countryCode);
    assertNull(location.countryName);
  }
}
