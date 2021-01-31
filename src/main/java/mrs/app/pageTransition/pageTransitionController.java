package mrs.app.pageTransition;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("transition")
public class pageTransitionController {

	@RequestMapping(method=RequestMethod.GET, params="coronameasure")
	public String transitToCorona(Model model) {
		return "coronameasures/coronameasures";
	}


	@RequestMapping(method=RequestMethod.GET, params="introductionOfrooms")
	public String transitToIntroduction(Model model) {
		return "introductionrooms/introductionrooms";
	}

	@RequestMapping(method=RequestMethod.GET, params="priceList")
	public String transitToPrice(Model model) {
		return "priceList/priceList";
	}
}
