package entity.projectile;

import controller.SharedEntity;
import controller.View;
import entity.Entity;
import entity.monster.Monster;
import input.Input;
import resource.Sounds;
import resource.Sprites;

public class WeaponProjectile extends Projectile{
	
	private final int NORMAL = 0;
	private final int ATTACH = 2;
	
	private int ageTick = 120;
	
	public WeaponProjectile(double x, double y) {
		super(Sprites.p_weapon, x, y, 
				Input.getMouseX()  + View.getInstance().getX(), 
				Input.getMouseY()  + View.getInstance().getY(), 30.);
				width = 180; //from 100 to 180 for presentation purpose
				height = 180;
	}
	
	@Override
	public void update() {
		move();
		if(ageTick <= 0) {
			SharedEntity.getInstance().getWeapon().setState(NORMAL);
			SharedEntity.getInstance().getWeapon().setVisible(true);
			this.destroy();
		} else {
			ageTick -= 1;
		}
	}
	
	@Override
	public int attack(Entity e) {
		Sounds.fx_targetLock.play();
		SharedEntity.getInstance().getWeapon().setState(ATTACH);
		SharedEntity.getInstance().getWeapon().setVisible(true);
		SharedEntity.getInstance().getWeapon().setAttach((Monster) e);
		destroy();
		return 0;
	}
}
