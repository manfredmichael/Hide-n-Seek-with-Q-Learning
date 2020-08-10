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
	void reset(){
		x = floor(random(w));
		y = floor(random(h));
		for (Obstacle o : obstacles) {
			if(x == o.x && y == o.y){
				reset();
			}
		}
	}
	void show(){
		noStroke();
		fill(255, 255, 0);
		rect(x * resolutionX, y * resolutionY, resolutionX, resolutionY);
	}
	void look(Tagger o){
		memX   = x;
		memY   = y;
		enemyX = o.x;
		enemyY = o.y;
	}
	void decide(){
		qt.step(enemyX - memX, enemyY - memY, this);
	}
	float action(int act){
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
	void limit(){
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