package com.mygdx.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch


class Drop : Game() {

    internal lateinit var batch: SpriteBatch
    internal lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        this.setScreen(MainMenuScreen(this))

    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        font.dispose()
    }
}