package resource.scene;

import constants.Const;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import main.Main;
import resource.Sounds;
import resource.Sprites;

public class LoadingScene extends Scene {
	
	private Pane root;
	private Canvas canvas;
	private Thread loadThread;
	

	private int animationFrame = 0;
	private boolean animFinished = false;
	
	public LoadingScene() {
		super(new Pane());
		root = (Pane)getRoot();
		root.setStyle("-fx-background-color: #FFFFFF;");
		root.setPadding(new Insets(0));
		
		setCursor(Cursor.DISAPPEAR);
		
		canvas = new Canvas(Const.WINDOW_WIDTH,Const.WINDOW_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		root.getChildren().add(canvas);
		
		
		loadThread = new Thread(new Runnable() {
				public void run() {
					Sprites.loadResource();
					Sounds.loadResouce();
					Scenes.loadResource();
					
					while (!animFinished) { //wait for load animation finish
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}
					}
				}
			});
		
		
		loadThread.start();
		
		playLoopAnimation(gc);
		
		new Thread( () -> { //wrap with Thread because join problem
			try {
				loadThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			playFadeOutAnimation(gc);
		}).start(); //wait for loading finish
		
		
	}
	
	
	private void playFadeOutAnimation(GraphicsContext gc) {
		FadeTransition ft = new FadeTransition(Duration.seconds(0.5),root);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(e->{
			Main.getStage().setScene(Scenes.getStartScene());
			((StartScene)Scenes.getStartScene()).getBgm().play();
		});	
		ft.play();
	}
	
	private void playLoopAnimation(GraphicsContext gc) {
		KeyFrame kf = new KeyFrame(Duration.seconds(1./60),e-> {
			animationFrame ++;
			gc.clearRect(0, 0, Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT);
			if(animationFrame <= 120) {
				gc.setGlobalAlpha(animationFrame/120.);
				gc.drawImage(Sprites.loading_banner[0], 300, 300);
			}
			else {
				gc.setGlobalAlpha(1);
				gc.drawImage(Sprites.loading_banner[0], 300, 300);
			}
			
		});
		
		Timeline animLoop = new Timeline(kf);
		animLoop.setCycleCount(180);
		animLoop.setOnFinished(e->{
			animFinished = true;
		});
		animLoop.play();
	}
	
	//Getter-Setter

	public Thread getLoadThread() {
		return loadThread;
	}

}
