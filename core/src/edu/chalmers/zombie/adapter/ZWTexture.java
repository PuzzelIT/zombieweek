package edu.chalmers.zombie.adapter;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by daniel on 5/28/2015.
 */
public class ZWTexture {
    private Texture texture;
    public ZWTexture(String filePath){
        texture = new Texture(filePath);
    }
    public ZWTexture(ZWPixmap zwPixmap){texture = new Texture(zwPixmap.getPixmap());}
    public ZWTexture(Texture texture){this.texture = texture;}
    public Texture getTexture(){
        return texture;
    }

    public void prepare(){
        this.texture.getTextureData().prepare();
    }

    public ZWPixmap consumeZWPixmap(){
        return  (new ZWPixmap(this.texture.getTextureData().consumePixmap()));
    }

    public void dispose(){this.texture.dispose();}

}
