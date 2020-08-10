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

void setup(){
	size(600, 600);

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

void draw(){
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

void mousePressed(){
	int x = int((mouseX - (mouseX % resolutionX)) / resolutionX);
	int y = int((mouseY - (mouseY % resolutionY)) / resolutionY); 

	if(!thereIsObstacle[x][y]){
		obstacles.add(new Obstacle(x, y));
		println("x:",x);
		println("y:",y);
	}
}