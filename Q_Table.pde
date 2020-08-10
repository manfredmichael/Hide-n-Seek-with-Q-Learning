class QTable{
	float epsilon       = 0.6;
	float START_EPSILON = epsilon;
	float EPSILON_DECAY = 50000;
	float MIN_EPSILON   = 0.1;
	float LEARNING_RATE = 0.5;
	float DISCOUNT      = 0.85;
	float [][][] table = new float[w * 2 - 1][h * 2 - 1][4];
	QTable(){
		for (int i = 0; i < w * 2 - 1; i++) {
			for (int j = 0; j < h * 2 - 1; j++) {
				for (int k = 0; k < 4; k++) {
					table[i][j][k] = random(-0.01, 0.01);
				}
			}
		}
	}

	void step(int x, int y, Tagger player){
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
	void step(int x, int y, Runner player){
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

int argMax(float [] num){
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