package pl.org.sendzimir.licznazielen;

import pl.org.sendzimir.licznazielen.services.RealWebApiService;
import pl.org.sendzimir.licznazielen.services.WebApiService;

import com.google.inject.Binder;
import com.google.inject.Module;

public class RealModule implements Module {
    @Override
    public void configure(Binder binder) {
    	binder.bind(WebApiService.class).to(RealWebApiService.class);
    }
}
