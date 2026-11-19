package io.github.some_example_name;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


import java.util.Arrays;

public class EndScreen implements Screen{

    final Minesweeper game;
    private Stage stage;
    private Skin skin;
    private String endMessage;
//    BitmapFont font32;


    public EndScreen(Minesweeper game) {
        this.game = game;

        System.out.println(game.gameScreen.game_over_status());



    }





    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("glassy-ui.json"));

        if (game.gameScreen.game_over_status()) {
            endMessage = "You Win";
        } else {
            endMessage = "You Lose";
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-VariableFont_wdth,wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;
        BitmapFont font32 = generator.generateFont(parameter);

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);


        Label.LabelStyle style = new Label.LabelStyle(font32, Color.BLACK);
        Label endText = new Label(endMessage, style);
        table.add(endText).padBottom(10);
        table.row();
        generator.dispose();



        TextButton myButton = new TextButton("Play Again?", skin);
        myButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.startScreen);            }
        });
        table.add(myButton).size(myButton.getPrefWidth(), myButton.getPrefHeight());



    }

    @Override
    public void render(float delta) {
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            game.setScreen(game.startScreen);
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
