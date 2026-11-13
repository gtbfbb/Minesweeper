package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.Texture;

import static io.github.some_example_name.Main.difficulty;


public class Minesweeper extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;
    public MainMenu mainMenu;
    public EndScreen endScreen;
    public StartScreen startScreen;
    public Main gameScreen;



    public void create() {
        batch = new SpriteBatch();
        // use libGDX's default font
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("C:\\Minesweeper\\assets\\Roboto\\Roboto-VariableFont_wdth,wght.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.size = 32;
        param.color = Color.WHITE;

        font = generator.generateFont(param);
        generator.dispose();

//        font = new BitmapFont();
        viewport = new FitViewport(Main.difficulties.get(difficulty)[1], Main.difficulties.get(difficulty)[2]);

        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight() * 2);

        mainMenu = new MainMenu(this);
        gameScreen = new Main(this);
        endScreen = new EndScreen(this);
        startScreen = new StartScreen(this);
        this.setScreen(startScreen);
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
