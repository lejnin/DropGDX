package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils

class GameScreen(var game : Drop) : Screen {

    private var camera: OrthographicCamera = OrthographicCamera()
    private var dropImage: Texture = Texture("droplet.png")
    private var bucketImage: Texture = Texture("bucket.png")
    private var dropSound: Sound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"))
    private var rainMusic: Music = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"))
    private var bucket: Rectangle = Rectangle()
    private var touchPos: Vector3 = Vector3()
    private var rainDrops: Array<Rectangle>
    private var lastDropTime: Long = 0
    private var dropGatchered : Int = 0
    private var timing : Int = 1000000000
    private var isGameOver = false

    init {

        camera.setToOrtho(false, 800f, 480f)

        rainMusic.isLooping = true
        rainMusic.play()

        bucket.x = (800 / 2 - 64 / 2).toFloat()
        bucket.y = 20f
        bucket.width = 64f
        bucket.height = 64f

        rainDrops = Array()
        spawnRaindrop()

    }

    private fun spawnRaindrop() {
        val rainDrop = Rectangle()
        rainDrop.x = MathUtils.random(0, 800 - 64).toFloat()
        rainDrop.y = 480f
        rainDrop.width = 64f
        rainDrop.height = 64f
        rainDrops.add(rainDrop)
        lastDropTime = TimeUtils.nanoTime()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        game.batch.projectionMatrix = camera.combined
        game.batch.begin()

        game.font.draw(game.batch, "$dropGatchered", 0f, 480f)
        if (isGameOver) {
            game.font.draw(game.batch, "Game Over!", (800 / 2).toFloat(), (480 / 2).toFloat())
            game.batch.end()
        } else {
            game.batch.draw(bucketImage, bucket.x, bucket.y)

            for (rainDrop in rainDrops) {
                game.batch.draw(dropImage, rainDrop.x, rainDrop.y)
            }

            game.batch.end()

            when {
                Gdx.input.isTouched -> {
                    touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
                    camera.unproject(touchPos)
                    bucket.x = (touchPos.x - 64 / 2).toInt().toFloat()
                }
                Gdx.input.isKeyPressed(Input.Keys.LEFT) -> bucket.x -= 200 * Gdx.graphics.deltaTime
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> bucket.x += 200 * Gdx.graphics.deltaTime
            }

            if (bucket.x < 0) bucket.x = 0f
            if (bucket.x > 800 - 64) bucket.x = (800 - 64).toFloat()

            if (!isGameOver && TimeUtils.nanoTime() - lastDropTime > timing) {
                spawnRaindrop()
                if (timing > 350000000) {
                    timing -= 5000000
                }
            }
        }

        val iterator = rainDrops.iterator()
        while (iterator.hasNext()) {
            val raindrop = iterator.next()
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.y + 64 < 0) {
                iterator.remove()
                //isGameOver = true
            }

            if (raindrop.overlaps(bucket)) {
                dropSound.play()
                iterator.remove()
                dropGatchered++
            }
        }
    }

    override fun hide() {

    }

    override fun show() {
        rainMusic.play()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {
        dropImage.dispose()
        bucketImage.dispose()
        dropSound.dispose()
        rainMusic.dispose()
    }
}