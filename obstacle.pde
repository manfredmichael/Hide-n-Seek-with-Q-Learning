

class Obstacle{
	int x;
	int y;
	Obstacle(int x, int y){
		this.x = x;
		this.y = y;
		thereIsObstacle[x][y] = true;
	}
	void show(){
		fill(145);
		rect(x * resolutionX, y * resolutionY, resolutionX, resolutionY);
	}
}