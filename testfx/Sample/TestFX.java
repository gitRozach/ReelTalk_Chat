package Sample;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import com.jfoenix.controls.JFXButton;

import controller.ReelTalkSession;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

class TestFX extends ApplicationTest {
	@BeforeAll
	public static void setUpClass() throws Exception {
		ApplicationTest.launch(ReelTalkSession.class);
	}
	
	protected Node findButtonById(String id) {
		if(id == null)
			return null;
		if(!id.contains("#"))
			return findButtonById("#" + id);
		return lookup((Button n) -> n.getId().equals(id)).query();
	}
	
	protected Node findButtonByText(String buttonText) {
		if(buttonText == null)
			return null;
		return lookup((Button b) -> b.getText().equals(buttonText)).query();
	}
	
	@Test
	public void clickOnButtons() {
		JFXButton b1 = (JFXButton) findButtonByText("B1");
		JFXButton b2 = (JFXButton) findButtonByText("B2");
		JFXButton b3 = (JFXButton) findButtonByText("B3");
		JFXButton b4 = (JFXButton) findButtonByText("B4");
		JFXButton b5 = (JFXButton) findButtonByText("B5");
		clickOn(b1);
		clickOn(b2);
		clickOn(b3);
		clickOn(b4);
		clickOn(b5);
		sleep(1000L);
	}
	
	@AfterEach
	public void tearDownClass() throws TimeoutException {
		FxToolkit.hideStage();
		release(new KeyCode[] {});
		release(new MouseButton[] {});
	}

}
