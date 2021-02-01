package mrs.app.pageTransition;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


//ページ遷移専用のコントローラ
@Controller
@RequestMapping("transition")
public class pageTransitionController {

	@RequestMapping(method=RequestMethod.GET, params="coronaMeasure")
	public String transitToCorona(Model model) {
		return "coronaMeasure/coronaMeasure";
	}


	@RequestMapping(method=RequestMethod.GET, params="introductionOfRooms")
	public String transitToIntroduction(Model model) {
		return "introductionRooms/introductionRooms";
	}

	@RequestMapping(method=RequestMethod.GET, params="priceList")
	public String transitToPrice(Model model) {
		return "priceList/priceList";
	}
}
