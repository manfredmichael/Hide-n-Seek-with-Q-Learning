import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class quick_Q_Learning extends PApplet {

int w = 10;
int h = 10;
float resolutionX;
float resolutionY;

Tagger tagger;
Runner runner;

int step = 0;

int SHOW_EVERY = 1000;

boolean [][] thereIsObstacle  = new boolean [w][h];

ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

public void setup(){
	

	resolutionX = width  / w;
	resolutionY = height / w;

	tagger = new Tagger();
	runner = new Runner();

	for (int x = 0; x < w; x++) {
		for (int y = 0; y < h; y++) {
			thereIsObstacle[x][y] = false;
		}
	}
}

public void draw(){
	if(step % SHOW_EVERY < 100){
		if(step % SHOW_EVERY == 0){
			runner.reset();
			tagger.reset();
		}
		
		frameRate(5);
	}
	else
		frameRate(30);
	background(51);

	for (Obstacle o : obstacles) {
		o.show();
	}

	runner.show();
	tagger.show();

	tagger.look(runner);
	runner.look(tagger);

	tagger.decide();
	runner.decide();
	step++;
}

public void mousePressed(){
	int x = PApplet.parseInt((mouseX - (mouseX % resolutionX)) / resolutionX);
	int y = PApplet.parseInt((mouseY - (mouseY % resolutionY)) / resolutionY); 

	if(!thereIsObstacle[x][y]){
		obstacles.add(new Obstacle(x, y));
		println("x:",x);
		println("y:",y);
	}
}
class QTable{
	float epsilon       = 0.6f;
	float START_EPSILON = epsilon;
	float EPSILON_DECAY = 50000;
	float MIN_EPSILON   = 0.1f;
	float LEARNING_RATE = 0.5f;
	float DISCOUNT      = 0.85f;
	float [][][] table = new float[w * 2 - 1][h * 2 - 1][4];
	QTable(){
		for (int i = 0; i < w * 2 - 1; i++) {
			for (int j = 0; j < h * 2 - 1; j++) {
				for (int k = 0; k < 4; k++) {
					table[i][j][k] = random(-0.01f, 0.01f);
				}
			}
		}
	}

	public void step(int x, int y, Tagger player){
		int action;
		x = x + w - 1;
		y = y + h - 1;
		if(random(1) > epsilon){
			action = argMax(table[x][y]);
		}
		else 
			action = floor(random(4));

		float reward = player.action(action);

		table[x][y][action] = (1 - LEARNING_RATE) * table[x][y][action] + (LEARNING_RATE * (reward + DISCOUNT * max(table[player.enemyX - player.x + w - 1][player.enemyY - player.y + h - 1])));

		if(epsilon > START_EPSILON / EPSILON_DECAY){
			epsilon -= START_EPSILON / EPSILON_DECAY;
		}
	}
	public void step(int x, int y, Runner player){
		int action;
		x = x + w - 1;
		y = y + h - 1;
		if(random(1) > epsilon){
			action = argMax(table[x][y]);
		}
		else 
			action = floor(random(4));

		float reward = player.action(action);

		table[x][y][action] = (1 - LEARNING_RATE) * table[x][y][action] + (LEARNING_RATE * (reward + DISCOUNT * max(table[tagger.x - player.x + w - 1][tagger.y - player.y + h - 1])));

		if(epsilon > MIN_EPSILON){
			epsilon -= START_EPSILON / EPSILON_DECAY;
		}
	}
}

public int argMax(float [] num){
	float max = max(num);
	for (int i = 0; i < num.length; i++) {
		if(num[i] == max)
			return i;
	}

	return 5;
}

// if(player.enemyX <= 0 || player.enemyX > 4)
// 			println("enemyX",player.enemyX);
// 		if(player.enemyY <= 0 || player.enemyY > 4)
// 			println("eenemyY",player.enemyY);
// 		if(x <= 0 || x > 8)
// 			println("x",x);
// 		if(y <= 0 || y > 8)
// 			println("y",y);
// 		if(action < 0 || action > 3)
// 			println("action", action);
// 		if(player.enemyX - player.x + w - 1 < 0 || player.enemyX - player.x + w - 1 > 8)
// 			println("a",player.enemyX - player.x + w - 1);
// 		if(player.enemyY - player.y + h - 1 < 0 || player.enemyY - player.y + h - 1 > 8)
// 			println(player.enemyY,player.y,"b",player.enemyY - player.y + h - 1);
class Runner{
	int x;
	int y;
	int memX;
	int memY;
	int enemyX;
	int enemyY;
	QTable qt;
	Runner(){
		qt = new QTable();
		reset();
	}
	public void reset(){
		x = floor(random(w));
		y = floor(random(h));
		for (Obstacle o : obstacles) {
			if(x == o.x && y == o.y){
				reset();
			}
		}
	}
	public void show(){
		noStroke();
		fill(255, 255, 0);
		rect(x * resolutionX, y * resolutionY, resolutionX, resolutionY);
	}
	public void look(Tagger o){
		memX   = x;
		memY   = y;
		enemyX = o.x;
		enemyY = o.y;
	}
	public void decide(){
		qt.step(enemyX - memX, enemyY - memY, this);
	}
	public float action(int act){
		if(act == 0){
			x++;
		} else if(act == 1){
			x--;
		} else if(act == 2){
			y--;
		} else if(act == 3){
			y++;
		}
		limit();
		float reward = 1;

		if(tagger.x == memX && tagger.y == memY){
			reward = -10;
			tagger.reset();
		}

		return reward;
	}
	public void limit(){
		if(x < 0)
			x = 0;
		else if(x > w - 1)
			x = w - 1;
		else if(y <  0)
			y = 0;
		else if(y > h - 1)
			y = h - 1;

		for (Obstacle o : obstacles) {
			if(x == o.x && y == o.y){
				y = memY;
				x = memX;
			}
		}
	}
}
class Tagger{
	int x;
	int y;
	int memX;
	int memY;
	int enemyX;
	int enemyY;
	QTable qt;
	Tagger(){
		qt = new QTable();
		reset();
	}
	public void reset(){
		x = floor(random(w));
		y = floor(random(h));
		for (Obstacle o : obstacles) {
			if(x == o.x && y == o.y){
				reset();
			}
		}
	}
	public void show(){
		noStroke();
		fill(255, 0, 0);
		rect(x * resolutionX, y * resolutionY, resolutionX, resolutionY);
	}
	public void look(Runner o){
		memX   = x;
		memY   = y;
		enemyX = o.x;
		enemyY = o.y;
	}
	public void decide(){
		qt.step(enemyX - memX, enemyY - memY, this);
	}
	public float action(int act){
		if(act == 0){
			x++;
			y++;
		} else if(act == 1){
			x--;
			y++;
		} else if(act == 2){
			x--;
			y--;
		} else if(act == 3){
			x++;
			y--;
		}
		limit();
		float reward = -1;

		if(tagger.x == enemyX && tagger.y == enemyY){
			reward = 20;
			runner.reset();
		}

		return reward;
	}
	public void limit(){
		if(x < 0)
			x = 0;
		if(x > w - 1)
			x = w - 1;
		if(y <  0)
			y = 0;
		if(y > h - 1)
			y = h - 1;

		int currentX = x;
		int currentY = y;

		for (Obstacle o : obstacles) {
			if(x == o.x && y == o.y){
				y = memY;
				for(Obstacle p : obstacles){
					if(x == p.x && y == p.y){
						y = currentY;
						x = memX;
						for(Obstacle q : obstacles){
							if(x == q.x && y == q.y){
								y = memY;
								x = memX;
							}
						}
					}
				}
			}
		}
	}	
}


class Obstacle{
	int x;
	int y;
	Obstacle(int x, int y){
		this.x = x;
		this.y = y;
		thereIsObstacle[x][y] = true;
	}
	public void show(){
		fill(145);
		rect(x * resolutionX, y * resolutionY, resolutionX, resolutionY);
	}
}
  public void settings() { 	size(600, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "quick_Q_Learning" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
