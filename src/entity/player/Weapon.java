package entity.player;

import controller.GameLogic;
import controller.SharedEntity;
import controller.View;
import entity.Entity;
import entity.monster.Monster;
import entity.particle.ScoreParticle;
import entity.projectile.PlayerBullet;
import entity.projectile.WeaponProjectile;
import exception.CharacterOutBoundException;
import input.Input;
import resource.Sounds;
import resource.Sprites;

public class Weapon extends Entity{
	private final double DIS = 100;
	
	private int fireCD = 0;
	
	private final int NORMAL = 0;
	private final int FIRE = 1;
	private final int ATTACH = 2;
	private int state = NORMAL;
	
	private Monster attach;
	

	public Weapon() {
		super(Sprites.p_weapon);
	}
	
	@Override
	public int getZ() {
		return 2;
	}

	@Override
	public void update() {
		processPos();
		processFire();
	}
	
	private void processPos() {
		if (state == NORMAL) {
			double dx = (Input.getMouseX() + View.getInstance().getX()) - SharedEntity.getInstance().getPlayer().getX();
			double dy = (Input.getMouseY()  + View.getInstance().getY()) - SharedEntity.getInstance().getPlayer().getY();
			
			double r = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			x = (dx / r) * DIS + SharedEntity.getInstance().getPlayer().getX();
			y = (dy / r) * DIS + SharedEntity.getInstance().getPlayer().getY();
		} else if (state == ATTACH) {
			double dx = attach.getX() - SharedEntity.getInstance().getPlayer().getX();
			double dy = attach.getY() - SharedEntity.getInstance().getPlayer().getY();
			
			double r = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			x = (dx / r) * DIS + attach.getX();
			y = (dy / r) * DIS + attach.getY();
			
		}
	}

	private void processFire() {
		if(Input.isLeftMouseActive() && fireCD == 0 && (state == NORMAL || state == ATTACH)) {
			Sounds.fx_fire.play();
			fireCD = 5;
			(new PlayerBullet(x,y)).create();
		} else if (Input.isRightMouseTrigger()) { //nested if else due to machanism of trigger can only be check once
			if (state == NORMAL) {
				Sounds.fx_skill.play();
				state = FIRE;
				visible = false;
				(new WeaponProjectile(x,y)).create();
			} else if (state == ATTACH) {
				Sounds.fx_dash.play();
				GameLogic.getInstance().setScore(GameLogic.getInstance().getScore() + 50);
				(new ScoreParticle(x,y,50)).create();
				SharedEntity.getInstance().getPlayer().setJumpCount(2);
				SharedEntity.getInstance().getPlayer().heal(80);
				SharedEntity.getInstance().getPlayer().setOnWarp();
				try {
					SharedEntity.getInstance().getPlayer().setX(x);
					SharedEntity.getInstance().getPlayer().setY(y);
					attach.destroy();
					state = NORMAL;
				} catch (CharacterOutBoundException e) {
					System.out.println("done");
					state = NORMAL;
					attach.destroy();
				}
			}
		}
		if(fireCD > 0) {
			fireCD--;
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public Monster getAttach() {
		return attach;
	}

	public void setAttach(Monster attach) {
		this.attach = attach;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}


		
	

}
