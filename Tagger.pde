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
		fill(255, 0, 0);
		rect(x * resolutionX, y * resolutionY, resolutionX, resolutionY);
	}
	void look(Runner o){
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
	void limit(){
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