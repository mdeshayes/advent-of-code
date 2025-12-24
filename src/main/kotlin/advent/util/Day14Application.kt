package advent.util

import advent.y2024.Day14
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

const val CELL_SIZE = 8f

class Day14Application(private val day14: Day14) : ApplicationAdapter() {

    private lateinit var shapeRenderer: ShapeRenderer
    private var count = 0
    private var paused = false
    override fun create() {
        shapeRenderer = ShapeRenderer()
    }

    override fun render() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            paused = true
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            paused = false
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            day14.moveRobots(-1)
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        day14.robots.forEach {
            shapeRenderer.color = Color.GREEN
            shapeRenderer.rect(it.position.first * CELL_SIZE, it.position.second * CELL_SIZE, CELL_SIZE, CELL_SIZE)
        }
        shapeRenderer.end()
        if (!paused) {
            count++
            day14.moveRobots(1)
        }
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

}